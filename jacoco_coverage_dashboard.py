#!/usr/bin/env python3
"""
Generate a polished Markdown coverage dashboard from JaCoCo XML reports.

Place this script at the repository root and run:

    python jacoco_coverage_dashboard.py

Default output:

    COVERAGE.md

The script searches recursively for JaCoCo XML files under target directories, for example:

    target/site/jacoco/jacoco.xml
    module-a/target/site/jacoco/jacoco.xml
    module-b/target/site/jacoco-aggregate/jacoco.xml

No third-party Python dependencies are required.
"""

from __future__ import annotations

import argparse
import datetime as dt
import html
import math
import xml.etree.ElementTree as ET
from collections import defaultdict
from dataclasses import dataclass, field
from pathlib import Path
from typing import Iterable, Sequence


COUNTER_TYPES = ("INSTRUCTION", "BRANCH", "LINE", "COMPLEXITY", "METHOD", "CLASS")

DEFAULT_THRESHOLDS = {
    "excellent": 90.0,
    "good": 80.0,
    "attention": 60.0,
}

COUNTER_LABELS = {
    "INSTRUCTION": "Instruction",
    "BRANCH": "Branch",
    "LINE": "Line",
    "COMPLEXITY": "Complexity",
    "METHOD": "Method",
    "CLASS": "Class",
}


@dataclass
class Counter:
    missed: int = 0
    covered: int = 0

    @property
    def total(self) -> int:
        return self.missed + self.covered

    @property
    def pct(self) -> float:
        if self.total == 0:
            return 100.0
        return (self.covered / self.total) * 100.0

    def add(self, other: "Counter") -> None:
        self.missed += other.missed
        self.covered += other.covered


@dataclass
class CoverageNode:
    name: str
    module: str
    package: str = ""
    kind: str = "node"
    source: Path | None = None
    counters: dict[str, Counter] = field(default_factory=lambda: {k: Counter() for k in COUNTER_TYPES})

    def pct(self, counter_type: str) -> float:
        return self.counters.get(counter_type, Counter()).pct

    def total(self, counter_type: str) -> int:
        return self.counters.get(counter_type, Counter()).total

    def missed(self, counter_type: str) -> int:
        return self.counters.get(counter_type, Counter()).missed

    def covered(self, counter_type: str) -> int:
        return self.counters.get(counter_type, Counter()).covered


@dataclass
class ReportData:
    generated_at: str
    root: Path
    report_files: list[Path]
    project: CoverageNode
    modules: list[CoverageNode]
    packages: list[CoverageNode]
    classes: list[CoverageNode]


def parse_counter(element):
    counter_type = element.attrib.get("type")
    if counter_type not in COUNTER_TYPES:
        return None
    return counter_type, Counter(
        missed=int(element.attrib.get("missed", "0")),
        covered=int(element.attrib.get("covered", "0")),
    )


def parse_counters(element) -> dict[str, Counter]:
    counters = {k: Counter() for k in COUNTER_TYPES}
    for counter_el in element.findall("counter"):
        parsed = parse_counter(counter_el)
        if parsed:
            counter_type, counter = parsed
            counters[counter_type].add(counter)
    return counters


def add_counters(target: dict[str, Counter], source: dict[str, Counter]) -> None:
    for counter_type, counter in source.items():
        target.setdefault(counter_type, Counter()).add(counter)


def infer_module_name(report_file: Path, root: Path) -> str:
    rel = report_file.relative_to(root)
    parts = rel.parts
    if parts and parts[0] == "target":
        return root.name
    if "target" in parts:
        target_index = parts.index("target")
        if target_index > 0:
            return "/".join(parts[:target_index])
    return report_file.parent.name


def discover_reports(root: Path, explicit_reports: Sequence[str] | None = None) -> list[Path]:
    if explicit_reports:
        reports = [Path(p).resolve() for p in explicit_reports]
        return [p for p in reports if p.exists() and p.is_file()]

    candidates = sorted(root.glob("**/target/**/jacoco*.xml"))
    return [
        p
        for p in candidates
        if p.is_file()
        and "site" in p.parts
        and p.name.endswith(".xml")
        and not any(part in {".git", ".idea", ".vscode"} for part in p.parts)
    ]


def parse_report_file(report_file: Path, root: Path) -> tuple[CoverageNode, list[CoverageNode], list[CoverageNode]]:
    tree = ET.parse(report_file)
    report = tree.getroot()
    module_name = infer_module_name(report_file, root)

    module_node = CoverageNode(
        name=module_name,
        module=module_name,
        kind="module",
        source=report_file,
        counters=parse_counters(report),
    )

    package_nodes: list[CoverageNode] = []
    class_nodes: list[CoverageNode] = []

    for package_el in report.findall("package"):
        package_name = package_el.attrib.get("name", "").replace("/", ".")
        package_node = CoverageNode(
            name=package_name or "(default package)",
            module=module_name,
            package=package_name,
            kind="package",
            source=report_file,
            counters=parse_counters(package_el),
        )
        package_nodes.append(package_node)

        for class_el in package_el.findall("class"):
            raw_class_name = class_el.attrib.get("name", "")
            class_name = raw_class_name.replace("/", ".")
            if package_name and class_name.startswith(package_name + "."):
                display_name = class_name
            elif package_name:
                display_name = f"{package_name}.{class_name.split('.')[-1]}"
            else:
                display_name = class_name or "(anonymous class)"

            class_nodes.append(
                CoverageNode(
                    name=display_name,
                    module=module_name,
                    package=package_name,
                    kind="class",
                    source=report_file,
                    counters=parse_counters(class_el),
                )
            )

    return module_node, package_nodes, class_nodes


def load_reports(root: Path, explicit_reports: Sequence[str] | None = None) -> ReportData:
    report_files = discover_reports(root, explicit_reports)
    if not report_files:
        raise SystemExit(
            "No JaCoCo XML reports found. Expected files like target/site/jacoco/jacoco.xml. "
            "Run your Maven/Gradle test coverage task first."
        )

    modules: list[CoverageNode] = []
    packages: list[CoverageNode] = []
    classes: list[CoverageNode] = []

    for report_file in report_files:
        module, module_packages, module_classes = parse_report_file(report_file, root)
        modules.append(module)
        packages.extend(module_packages)
        classes.extend(module_classes)

    aggregate_reports = [
        m for m in modules if m.source and any("aggregate" in part.lower() for part in m.source.parts)
    ]

    project = CoverageNode(name=root.name, module=root.name, kind="project")
    if len(aggregate_reports) == 1:
        add_counters(project.counters, aggregate_reports[0].counters)
    else:
        for module in modules:
            add_counters(project.counters, module.counters)

    return ReportData(
        generated_at=dt.datetime.now().astimezone().strftime("%Y-%m-%d %H:%M:%S %Z"),
        root=root,
        report_files=report_files,
        project=project,
        modules=modules,
        packages=packages,
        classes=classes,
    )


def status_for_pct(pct: float, thresholds: dict[str, float] = DEFAULT_THRESHOLDS) -> tuple[str, str]:
    if pct >= thresholds["excellent"]:
        return "Excellent", "🟢"
    if pct >= thresholds["good"]:
        return "Good", "🟡"
    if pct >= thresholds["attention"]:
        return "Needs attention", "🟠"
    return "Critical", "🔴"


def color_for_pct(pct: float, thresholds: dict[str, float] = DEFAULT_THRESHOLDS) -> str:
    if pct >= thresholds["excellent"]:
        return "#2da44e"
    if pct >= thresholds["good"]:
        return "#bf8700"
    if pct >= thresholds["attention"]:
        return "#fb8500"
    return "#cf222e"


def fmt_pct(value: float) -> str:
    return f"{value:.2f}%"


def fmt_int(value: int) -> str:
    return f"{value:,}"


def esc(text: object) -> str:
    return html.escape(str(text), quote=True)


def markdown_escape(text: object) -> str:
    return str(text).replace("\\", "\\\\").replace("|", "\\|").replace("\n", " ")


def progress_bar(pct: float, width: int = 140, height: int = 10, label: bool = False) -> str:
    pct = max(0.0, min(100.0, pct))
    color = color_for_pct(pct)
    text = f'<span style="font-size: 12px;">{fmt_pct(pct)}</span>' if label else ""
    return (
        f'<div style="display:flex;align-items:center;gap:8px;min-width:{width + 58}px;">'
        f'<div style="width:{width}px;height:{height}px;background:#eaeef2;border-radius:999px;overflow:hidden;">'
        f'<div style="width:{pct:.2f}%;height:{height}px;background:{color};border-radius:999px;"></div>'
        f'</div>{text}</div>'
    )


def badge(label: str, pct: float) -> str:
    color = color_for_pct(pct).replace("#", "")
    label_part = label.replace("-", "--").replace("_", "__").replace(" ", "%20")
    value_part = fmt_pct(pct).replace("%", "%25")
    return f"![{esc(label)}](https://img.shields.io/badge/{label_part}-{value_part}-{color})"


def coverage_sentence(data: ReportData) -> str:
    line = data.project.pct("LINE")
    branch = data.project.pct("BRANCH")
    method = data.project.pct("METHOD")
    class_pct = data.project.pct("CLASS")
    line_status, line_icon = status_for_pct(line)
    branch_status, branch_icon = status_for_pct(branch)

    if line >= 85 and branch >= 75:
        tone = (
            "The project has a strong coverage foundation. Line coverage is healthy, and branch "
            "coverage suggests that many decision paths are being exercised."
        )
    elif line >= 70 and branch < 60:
        tone = (
            "The project has reasonable statement coverage, but branch coverage is the main risk. "
            "Tests are reaching code, yet many conditional paths are probably untested."
        )
    elif line < 60:
        tone = (
            "The project coverage is still fragile. The biggest value will come from covering the "
            "largest uncovered classes and the packages with the most missed lines."
        )
    else:
        tone = (
            "The project has a usable coverage baseline, with clear room to improve around lower "
            "covered packages and branch-heavy code."
        )

    return (
        f"{tone} Current headline: {line_icon} line coverage is **{fmt_pct(line)}** "
        f"({line_status.lower()}), while {branch_icon} branch coverage is **{fmt_pct(branch)}** "
        f"({branch_status.lower()}). Method coverage is **{fmt_pct(method)}** and class coverage is "
        f"**{fmt_pct(class_pct)}**."
    )


def make_table(headers: Sequence[str], rows: Iterable[Sequence[object]]) -> str:
    header_line = "| " + " | ".join(headers) + " |"
    separator = "| " + " | ".join("---" for _ in headers) + " |"
    row_lines = ["| " + " | ".join(markdown_escape(cell) for cell in row) + " |" for row in rows]
    return "\n".join([header_line, separator, *row_lines])


def counter_summary_rows(node: CoverageNode) -> list[list[object]]:
    rows = []
    for counter_type in COUNTER_TYPES:
        counter = node.counters[counter_type]
        status, icon = status_for_pct(counter.pct)
        rows.append([
            COUNTER_LABELS[counter_type],
            f"{icon} {status}",
            fmt_pct(counter.pct),
            progress_bar(counter.pct),
            fmt_int(counter.covered),
            fmt_int(counter.missed),
            fmt_int(counter.total),
        ])
    return rows


def svg_bar_chart(
    title: str,
    items: Sequence[tuple[str, float]],
    *,
    width: int = 900,
    row_height: int = 34,
    label_width: int = 250,
    value_suffix: str = "%",
) -> str:
    if not items:
        return ""

    chart_height = 54 + row_height * len(items)
    bar_width = width - label_width - 110
    max_value = max(100.0 if value_suffix == "%" else 0.0, max(v for _, v in items) or 1.0)

    parts = [
        f'<svg width="{width}" height="{chart_height}" viewBox="0 0 {width} {chart_height}" '
        f'xmlns="http://www.w3.org/2000/svg" role="img" aria-label="{esc(title)}">',
        f'<rect width="{width}" height="{chart_height}" rx="16" fill="#f6f8fa"/>',
        f'<text x="24" y="32" font-family="Inter, Segoe UI, Arial, sans-serif" font-size="18" '
        f'font-weight="700" fill="#24292f">{esc(title)}</text>',
    ]

    x0 = label_width
    y = 58
    for label, value in items:
        bar_len = 0 if max_value == 0 else (value / max_value) * bar_width
        color = color_for_pct(value if value_suffix == "%" else min(100, value))
        parts.extend([
            f'<text x="24" y="{y + 18}" font-family="Inter, Segoe UI, Arial, sans-serif" '
            f'font-size="12" fill="#57606a">{esc(label[:48])}</text>',
            f'<rect x="{x0}" y="{y}" width="{bar_width}" height="18" rx="9" fill="#eaeef2"/>',
            f'<rect x="{x0}" y="{y}" width="{bar_len:.2f}" height="18" rx="9" fill="{color}"/>',
            f'<text x="{x0 + bar_width + 16}" y="{y + 14}" font-family="Inter, Segoe UI, Arial, sans-serif" '
            f'font-size="12" font-weight="600" fill="#24292f">{value:.2f}{esc(value_suffix)}</text>',
        ])
        y += row_height

    parts.append("</svg>")
    return "\n".join(parts)


def svg_donut_chart(title: str, pct: float, *, width: int = 320, height: int = 220) -> str:
    pct = max(0.0, min(100.0, pct))
    radius = 70
    circumference = 2 * math.pi * radius
    dash = circumference * pct / 100.0
    gap = circumference - dash
    color = color_for_pct(pct)
    status, icon = status_for_pct(pct)

    return f"""
<svg width="{width}" height="{height}" viewBox="0 0 {width} {height}" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="{esc(title)}">
  <rect width="{width}" height="{height}" rx="18" fill="#f6f8fa"/>
  <text x="24" y="34" font-family="Inter, Segoe UI, Arial, sans-serif" font-size="17" font-weight="700" fill="#24292f">{esc(title)}</text>
  <circle cx="{width // 2}" cy="108" r="{radius}" fill="none" stroke="#eaeef2" stroke-width="18"/>
  <circle cx="{width // 2}" cy="108" r="{radius}" fill="none" stroke="{color}" stroke-width="18"
          stroke-linecap="round" transform="rotate(-90 {width // 2} 108)"
          stroke-dasharray="{dash:.2f} {gap:.2f}"/>
  <text x="{width // 2}" y="104" text-anchor="middle" font-family="Inter, Segoe UI, Arial, sans-serif" font-size="30" font-weight="800" fill="#24292f">{fmt_pct(pct)}</text>
  <text x="{width // 2}" y="132" text-anchor="middle" font-family="Inter, Segoe UI, Arial, sans-serif" font-size="13" fill="#57606a">{esc(icon)} {esc(status)}</text>
</svg>
""".strip()


def risk_score(node: CoverageNode) -> float:
    missed_lines = node.missed("LINE")
    missed_branches = node.missed("BRANCH")
    missed_methods = node.missed("METHOD")
    line_gap = 100.0 - node.pct("LINE")
    branch_gap = 100.0 - node.pct("BRANCH")
    size_factor = math.log1p(node.total("LINE") + node.total("BRANCH") + node.total("METHOD"))
    return (missed_lines * 1.6 + missed_branches * 1.2 + missed_methods * 0.7 + line_gap + branch_gap * 0.5) * size_factor


def sorted_by_low_coverage(nodes: Sequence[CoverageNode], counter_type: str, min_total: int = 1) -> list[CoverageNode]:
    return sorted(
        [n for n in nodes if n.total(counter_type) >= min_total],
        key=lambda n: (n.pct(counter_type), -n.total(counter_type), n.name),
    )


def sorted_by_risk(nodes: Sequence[CoverageNode], limit: int) -> list[CoverageNode]:
    return sorted(nodes, key=risk_score, reverse=True)[:limit]


def distribution_buckets(nodes: Sequence[CoverageNode], counter_type: str) -> list[tuple[str, int]]:
    counts = {"90–100%": 0, "80–89%": 0, "60–79%": 0, "0–59%": 0}
    for node in nodes:
        pct = node.pct(counter_type)
        if pct >= 90:
            counts["90–100%"] += 1
        elif pct >= 80:
            counts["80–89%"] += 1
        elif pct >= 60:
            counts["60–79%"] += 1
        else:
            counts["0–59%"] += 1
    return list(counts.items())


def narrative_insights(data: ReportData) -> list[str]:
    insights: list[str] = []
    project = data.project
    line = project.pct("LINE")
    branch = project.pct("BRANCH")
    method = project.pct("METHOD")

    if branch + 15 < line:
        insights.append(
            f"Branch coverage trails line coverage by **{line - branch:.2f} percentage points**, "
            "which usually means tests execute code but do not fully exercise decision paths."
        )

    if method > line + 10:
        insights.append(
            f"Method coverage is **{fmt_pct(method)}**, noticeably above line coverage. "
            "Many methods are touched, but larger method bodies still have uncovered paths."
        )

    risky_classes = sorted_by_risk(data.classes, 3)
    if risky_classes:
        names = ", ".join(f"`{c.name}`" for c in risky_classes)
        insights.append(
            f"The highest-impact improvement targets are {names}. These combine uncovered lines, "
            "missed branches, and class size."
        )

    weak_packages = sorted_by_low_coverage(data.packages, "LINE", min_total=5)[:3]
    if weak_packages:
        names = ", ".join(f"`{p.name}`" for p in weak_packages)
        insights.append(f"The weakest packages by line coverage are {names}.")

    if not insights:
        insights.append(
            "Coverage is broadly consistent across the main counters. Further improvements should focus on "
            "business-critical flows rather than chasing percentages alone."
        )

    return insights


def render_markdown(data: ReportData, top_n: int) -> str:
    project = data.project

    module_rows = []
    for module in sorted(data.modules, key=lambda m: (m.name, str(m.source))):
        module_rows.append([
            module.name,
            fmt_pct(module.pct("LINE")),
            progress_bar(module.pct("LINE")),
            fmt_pct(module.pct("BRANCH")),
            fmt_pct(module.pct("METHOD")),
            fmt_int(module.missed("LINE")),
            module.source.relative_to(data.root) if module.source else "",
        ])

    risky_classes = sorted_by_risk(data.classes, top_n)
    risky_rows = []
    for idx, cls in enumerate(risky_classes, 1):
        risky_rows.append([
            idx,
            f"`{cls.name}`",
            cls.module,
            fmt_pct(cls.pct("LINE")),
            progress_bar(cls.pct("LINE")),
            fmt_pct(cls.pct("BRANCH")),
            fmt_int(cls.missed("LINE")),
            fmt_int(cls.missed("BRANCH")),
            f"{risk_score(cls):.1f}",
        ])

    weakest_packages = sorted_by_low_coverage(data.packages, "LINE", min_total=5)[:top_n]
    package_rows = []
    for pkg in weakest_packages:
        package_rows.append([
            f"`{pkg.name}`",
            pkg.module,
            fmt_pct(pkg.pct("LINE")),
            progress_bar(pkg.pct("LINE")),
            fmt_pct(pkg.pct("BRANCH")),
            fmt_int(pkg.missed("LINE")),
            fmt_int(pkg.total("LINE")),
        ])

    best_classes = sorted(
        [c for c in data.classes if c.total("LINE") >= 10],
        key=lambda c: (-c.pct("LINE"), -c.total("LINE"), c.name),
    )[:top_n]
    best_rows = []
    for cls in best_classes:
        best_rows.append([
            f"`{cls.name}`",
            cls.module,
            fmt_pct(cls.pct("LINE")),
            progress_bar(cls.pct("LINE")),
            fmt_int(cls.total("LINE")),
            fmt_pct(cls.pct("BRANCH")),
        ])

    class_distribution = distribution_buckets(data.classes, "LINE")
    distribution_rows = [[bucket, count] for bucket, count in class_distribution]

    lowest_chart_items = [
        (pkg.name if len(pkg.name) <= 48 else "…" + pkg.name[-47:], pkg.pct("LINE"))
        for pkg in weakest_packages[:10]
    ]
    max_risk = risk_score(risky_classes[0]) if risky_classes else 1.0
    risk_chart_items = [
        (cls.name.split(".")[-1], min(100.0, risk_score(cls) / (max_risk or 1.0) * 100.0))
        for cls in risky_classes[:10]
    ]

    report_files_list = "\n".join(f"- `{markdown_escape(p.relative_to(data.root))}`" for p in data.report_files)

    return f"""# Coverage dashboard

{badge("line", project.pct("LINE"))}
{badge("branch", project.pct("BRANCH"))}
{badge("method", project.pct("METHOD"))}
{badge("class", project.pct("CLASS"))}

> Generated on **{data.generated_at}** from **{len(data.report_files)}** JaCoCo XML report(s).

---

## Executive summary

{coverage_sentence(data)}

### Headline coverage

<table>
<tr>
<td>{svg_donut_chart("Line coverage", project.pct("LINE"))}</td>
<td>{svg_donut_chart("Branch coverage", project.pct("BRANCH"))}</td>
<td>{svg_donut_chart("Method coverage", project.pct("METHOD"))}</td>
</tr>
</table>

## Coverage scorecard

{make_table(
    ["Metric", "Status", "Coverage", "Visual", "Covered", "Missed", "Total"],
    counter_summary_rows(project),
)}

## What the numbers are saying

{"".join(f"- {insight}\n" for insight in narrative_insights(data))}

## Module overview

{make_table(
    ["Module", "Line", "Visual", "Branch", "Method", "Missed lines", "Report"],
    module_rows,
)}

## Coverage distribution by class

This shows whether coverage is concentrated in a few well-tested classes or spread evenly across the codebase.

{make_table(["Line coverage bucket", "Classes"], distribution_rows)}

## Weakest packages by line coverage

These packages are the best starting point when you want broad improvement.

{svg_bar_chart("Lowest package line coverage", lowest_chart_items) if lowest_chart_items else "_No package data available._"}

{make_table(
    ["Package", "Module", "Line", "Visual", "Branch", "Missed lines", "Total lines"],
    package_rows,
) if package_rows else "_No package data available._"}

## Highest-impact classes to test next

Risk score combines missed lines, missed branches, missed methods, and class size. A high score does not mean the class is bad; it means new tests here should have the biggest coverage payoff.

{svg_bar_chart("Relative test-impact risk", risk_chart_items, value_suffix="") if risk_chart_items else "_No class data available._"}

{make_table(
    ["#", "Class", "Module", "Line", "Visual", "Branch", "Missed lines", "Missed branches", "Risk score"],
    risky_rows,
) if risky_rows else "_No class data available._"}

## Best-covered meaningful classes

Classes with at least 10 coverable lines and the strongest line coverage.

{make_table(
    ["Class", "Module", "Line", "Visual", "Total lines", "Branch"],
    best_rows,
) if best_rows else "_No class data available._"}

## Full package table

{make_table(
    ["Package", "Module", "Line", "Branch", "Method", "Class", "Missed lines"],
    [
        [
            f"`{{pkg.name}}`",
            pkg.module,
            fmt_pct(pkg.pct("LINE")),
            fmt_pct(pkg.pct("BRANCH")),
            fmt_pct(pkg.pct("METHOD")),
            fmt_pct(pkg.pct("CLASS")),
            fmt_int(pkg.missed("LINE")),
        ]
        for pkg in sorted(data.packages, key=lambda p: (p.module, p.name))
    ],
) if data.packages else "_No package data available._"}

## Full class table

<details>
<summary>Show all classes</summary>

{make_table(
    ["Class", "Module", "Line", "Branch", "Method", "Missed lines", "Missed branches"],
    [
        [
            f"`{{cls.name}}`",
            cls.module,
            fmt_pct(cls.pct("LINE")),
            fmt_pct(cls.pct("BRANCH")),
            fmt_pct(cls.pct("METHOD")),
            fmt_int(cls.missed("LINE")),
            fmt_int(cls.missed("BRANCH")),
        ]
        for cls in sorted(data.classes, key=lambda c: (c.module, c.package, c.name))
    ],
) if data.classes else "_No class data available._"}

</details>

## Reports used

{report_files_list}

---

## How to read this dashboard

- **Line coverage** tells how much executable code was touched.
- **Branch coverage** tells whether conditionals and decision paths were exercised.
- **Method coverage** tells whether tests reach API surfaces.
- **Risk score** prioritizes where tests will likely buy the most confidence and coverage.
- Use the weakest packages for broad strategy, then the highest-impact classes for concrete next actions.

<!-- Generated by jacoco_coverage_dashboard.py -->
"""


def main() -> None:
    parser = argparse.ArgumentParser(description="Generate a polished Markdown dashboard from JaCoCo XML coverage reports.")
    parser.add_argument("--root", default=".", help="Repository root. Default: current directory.")
    parser.add_argument("--output", default="COVERAGE.md", help="Markdown output file. Default: COVERAGE.md.")
    parser.add_argument("--top", type=int, default=15, help="Number of top packages/classes to show. Default: 15.")
    parser.add_argument(
        "--report",
        action="append",
        help="Explicit JaCoCo XML report path. Can be passed multiple times. If omitted, searches under **/target/**/jacoco*.xml.",
    )
    args = parser.parse_args()

    root = Path(args.root).resolve()
    output_path = Path(args.output)
    if not output_path.is_absolute():
        output_path = root / output_path

    data = load_reports(root, args.report)
    markdown = render_markdown(data, args.top)
    output_path.write_text(markdown, encoding="utf-8")

    print(f"Coverage dashboard written to {output_path}")
    print(f"Reports parsed: {len(data.report_files)}")
    print(f"Line coverage: {fmt_pct(data.project.pct('LINE'))}")
    print(f"Branch coverage: {fmt_pct(data.project.pct('BRANCH'))}")


if __name__ == "__main__":
    main()
