import csv
from collections import defaultdict
from pathlib import Path
import xml.etree.ElementTree as ET


REPORT_DIR = Path("./target/jacoco-report")
XML_REPORT = REPORT_DIR / "jacoco.xml"
CSV_REPORT = REPORT_DIR / "jacoco.csv"
OUTPUT_REPORT = Path("coverage_report.md")
TOP_N = 10


def percentage(covered, missed):
    total = covered + missed
    return (covered / total) * 100 if total > 0 else 0.0


def parse_root_metrics(root):
    metrics = {
        "INSTRUCTION": {"covered": 0, "missed": 0},
        "LINE": {"covered": 0, "missed": 0},
        "COMPLEXITY": {"covered": 0, "missed": 0},
        "METHOD": {"covered": 0, "missed": 0},
        "CLASS": {"covered": 0, "missed": 0},
    }

    for counter in root.findall("counter"):
        counter_type = counter.get("type")
        if counter_type in metrics:
            metrics[counter_type]["covered"] += int(counter.get("covered"))
            metrics[counter_type]["missed"] += int(counter.get("missed"))

    return metrics


def parse_csv_rows(file_path):
    with file_path.open(encoding="utf-8") as file:
        return list(csv.DictReader(file))


def build_package_summary(rows):
    packages = defaultdict(
        lambda: {
            "instruction_covered": 0,
            "instruction_missed": 0,
            "line_covered": 0,
            "line_missed": 0,
            "branch_covered": 0,
            "branch_missed": 0,
            "complexity_covered": 0,
            "complexity_missed": 0,
            "method_covered": 0,
            "method_missed": 0,
            "class_count": 0,
        }
    )

    for row in rows:
        package = packages[row["PACKAGE"]]
        package["instruction_covered"] += int(row["INSTRUCTION_COVERED"])
        package["instruction_missed"] += int(row["INSTRUCTION_MISSED"])
        package["line_covered"] += int(row["LINE_COVERED"])
        package["line_missed"] += int(row["LINE_MISSED"])
        package["branch_covered"] += int(row["BRANCH_COVERED"])
        package["branch_missed"] += int(row["BRANCH_MISSED"])
        package["complexity_covered"] += int(row["COMPLEXITY_COVERED"])
        package["complexity_missed"] += int(row["COMPLEXITY_MISSED"])
        package["method_covered"] += int(row["METHOD_COVERED"])
        package["method_missed"] += int(row["METHOD_MISSED"])
        package["class_count"] += 1

    return packages


def shorten_package_name(package_name):
    prefix = "br.org.catolicasc.pug."
    if package_name.startswith(prefix):
        return package_name[len(prefix):]
    return package_name


def top_low_coverage_packages(packages):
    candidates = []
    for package_name, values in packages.items():
        total_lines = values["line_covered"] + values["line_missed"]
        if total_lines == 0:
            continue

        candidates.append(
            {
                "package": package_name,
                "display_name": shorten_package_name(package_name),
                "line_covered": values["line_covered"],
                "line_missed": values["line_missed"],
                "instruction_covered": values["instruction_covered"],
                "instruction_missed": values["instruction_missed"],
                "class_count": values["class_count"],
                "line_percent": percentage(values["line_covered"], values["line_missed"]),
            }
        )

    return sorted(
        candidates,
        key=lambda item: (item["line_percent"], -item["line_missed"], item["package"]),
    )[:TOP_N]


def top_missed_line_classes(rows):
    candidates = []
    for row in rows:
        line_missed = int(row["LINE_MISSED"])
        line_covered = int(row["LINE_COVERED"])
        total_lines = line_covered + line_missed
        if total_lines == 0:
            continue

        candidates.append(
            {
                "package": row["PACKAGE"],
                "class_name": row["CLASS"],
                "full_name": f'{shorten_package_name(row["PACKAGE"])}.{row["CLASS"]}',
                "line_missed": line_missed,
                "line_covered": line_covered,
                "instruction_missed": int(row["INSTRUCTION_MISSED"]),
                "instruction_covered": int(row["INSTRUCTION_COVERED"]),
                "method_missed": int(row["METHOD_MISSED"]),
                "method_covered": int(row["METHOD_COVERED"]),
                "line_percent": percentage(line_covered, line_missed),
            }
        )

    return sorted(
        candidates,
        key=lambda item: (-item["line_missed"], item["line_percent"], item["full_name"]),
    )[:TOP_N]


def package_distribution(packages):
    candidates = []
    for package_name, values in packages.items():
        total_lines = values["line_covered"] + values["line_missed"]
        if total_lines == 0:
            continue

        candidates.append(
            {
                "package": package_name,
                "display_name": shorten_package_name(package_name),
                "total_lines": total_lines,
                "line_percent": percentage(values["line_covered"], values["line_missed"]),
                "class_count": values["class_count"],
            }
        )

    return sorted(candidates, key=lambda item: (-item["total_lines"], item["package"]))[:TOP_N]


def generate_markdown(report_name, metrics, rows, packages):
    line_percent = percentage(metrics["LINE"]["covered"], metrics["LINE"]["missed"])
    instruction_percent = percentage(
        metrics["INSTRUCTION"]["covered"], metrics["INSTRUCTION"]["missed"]
    )
    method_percent = percentage(metrics["METHOD"]["covered"], metrics["METHOD"]["missed"])
    class_percent = percentage(metrics["CLASS"]["covered"], metrics["CLASS"]["missed"])

    low_packages = top_low_coverage_packages(packages)
    missed_classes = top_missed_line_classes(rows)
    distributions = package_distribution(packages)

    lines = [
        f"# Relatorio de Cobertura de Testes - {report_name}",
        "",
        "## Resumo Executivo",
        "",
        f"- Cobertura de linhas: **{line_percent:.2f}%** "
        f'({metrics["LINE"]["covered"]} cobertas, {metrics["LINE"]["missed"]} perdidas).',
        f"- Cobertura de instrucoes: **{instruction_percent:.2f}%** "
        f'({metrics["INSTRUCTION"]["covered"]} cobertas, {metrics["INSTRUCTION"]["missed"]} perdidas).',
        f"- Cobertura de metodos: **{method_percent:.2f}%**. Cobertura de classes: **{class_percent:.2f}%**.",
        f"- Classes analisadas no CSV do JaCoCo: **{len(rows)}**.",
        f"- Pacotes com linhas rastreadas: **{len(packages)}**.",
        "",
        "## Totais Gerais",
        "",
        "| Metrica | Cobertas | Perdidas | Total | % Cobertura |",
        "| :--- | ---: | ---: | ---: | ---: |",
    ]

    for metric_name, values in metrics.items():
        covered = values["covered"]
        missed = values["missed"]
        total = covered + missed
        lines.append(
            f"| {metric_name.capitalize()} | {covered} | {missed} | {total} | "
            f"{percentage(covered, missed):.2f}% |"
        )

    lines.extend(
        [
            "",
            "## Pacotes com Menor Cobertura de Linhas",
            "",
            "| Pacote | % Linhas | Linhas Perdidas | Linhas Cobertas | Instrucoes Perdidas | Classes |",
            "| :--- | ---: | ---: | ---: | ---: | ---: |",
        ]
    )

    for item in low_packages:
        lines.append(
            f'| `{item["display_name"]}` | {item["line_percent"]:.2f}% | '
            f'{item["line_missed"]} | {item["line_covered"]} | '
            f'{item["instruction_missed"]} | {item["class_count"]} |'
        )

    lines.extend(
        [
            "",
            "## Classes com Mais Linhas Perdidas",
            "",
            "| Classe | % Linhas | Linhas Perdidas | Linhas Cobertas | Instrucoes Perdidas | Metodos Perdidos |",
            "| :--- | ---: | ---: | ---: | ---: | ---: |",
        ]
    )

    for item in missed_classes:
        lines.append(
            f'| `{item["full_name"]}` | {item["line_percent"]:.2f}% | '
            f'{item["line_missed"]} | {item["line_covered"]} | '
            f'{item["instruction_missed"]} | {item["method_missed"]} |'
        )

    lines.extend(
        [
            "",
            "## Pacotes com Maior Volume de Codigo Rastreado",
            "",
            "| Pacote | Total de Linhas | % Linhas | Classes |",
            "| :--- | ---: | ---: | ---: |",
        ]
    )

    for item in distributions:
        lines.append(
            f'| `{item["display_name"]}` | {item["total_lines"]} | '
            f'{item["line_percent"]:.2f}% | {item["class_count"]} |'
        )

    lines.extend(
        [
            "",
            "## Visualizacao Mermaid",
            "",
            "```mermaid",
            "pie title Cobertura Geral (Linhas)",
            f'    "Cobertas" : {metrics["LINE"]["covered"]}',
            f'    "Perdidas" : {metrics["LINE"]["missed"]}',
            "```",
            "",
            "## Leitura Rapida",
            "",
            "- A tabela de pacotes destaca onde a cobertura esta mais fragil no nivel em que o build costuma falhar.",
            "- A tabela de classes mostra os principais candidatos para novos testes quando a meta de um pacote cai.",
            "- O volume de codigo rastreado ajuda a separar pacote pequeno com cobertura ruim de pacote grande com impacto real.",
            "",
        ]
    )

    return "\n".join(lines)


def generate_coverage_report():
    if not XML_REPORT.exists():
        print(f"Erro: arquivo nao encontrado em {XML_REPORT}")
        return

    if not CSV_REPORT.exists():
        print(f"Erro: arquivo nao encontrado em {CSV_REPORT}")
        return

    tree = ET.parse(XML_REPORT)
    root = tree.getroot()

    metrics = parse_root_metrics(root)
    rows = parse_csv_rows(CSV_REPORT)
    packages = build_package_summary(rows)

    markdown = generate_markdown(root.get("name"), metrics, rows, packages)
    OUTPUT_REPORT.write_text(markdown, encoding="utf-8")

    print(f"Relatorio '{OUTPUT_REPORT}' gerado com sucesso usando {XML_REPORT} e {CSV_REPORT}")


if __name__ == "__main__":
    generate_coverage_report()
