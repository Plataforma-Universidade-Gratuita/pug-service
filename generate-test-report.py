import xml.etree.ElementTree as ET
import os

def generate_coverage_report():
    file_path = './target/jacoco-report/jacoco.xml'

    if not os.path.exists(file_path):
        print(f"Erro: Arquivo não encontrado em {file_path}")
        return

    tree = ET.parse(file_path)
    root = tree.getroot()

    # Estrutura para armazenar os totais
    metrics = {
        "INSTRUCTION": {"covered": 0, "missed": 0},
        "LINE": {"covered": 0, "missed": 0},
        "COMPLEXITY": {"covered": 0, "missed": 0},
        "METHOD": {"covered": 0, "missed": 0},
        "CLASS": {"covered": 0, "missed": 0}
    }

    # Procura os elementos 'counter' no nível raiz do relatório
    # (o Jacoco usa esses para os totais gerais do projeto)
    for counter in root.findall('counter'):
        ctype = counter.get('type')
        if ctype in metrics:
            metrics[ctype]["covered"] += int(counter.get('covered'))
            metrics[ctype]["missed"] += int(counter.get('missed'))

    # Geração do MD
    md_content = "# Relatório de Cobertura de Testes - " + root.get('name') + "\n\n"
    md_content += "| Métrica | Cobertas | Perdidas | Total | % Cobertura |\n"
    md_content += "| :--- | :---: | :---: | :---: | :---: |\n"

    for metric, values in metrics.items():
        covered = values['covered']
        missed = values['missed']
        total = covered + missed
        percent = (covered / total) * 100 if total > 0 else 0
        md_content += f"| {metric.capitalize()} | {covered} | {missed} | {total} | {percent:.2f}% |\n"

    md_content += "\n## Visualização Mermaid\n\n```mermaid\npie title Cobertura Geral (Linhas)\n"
    md_content += f'    "Cobertas" : {metrics["LINE"]["covered"]}\n'
    md_content += f'    "Perdidas" : {metrics["LINE"]["missed"]}\n'
    md_content += "```"

    with open("coverage_report.md", "w", encoding="utf-8") as f:
            f.write(md_content)
    print("Relatório 'coverage_report.md' gerado com sucesso lendo de " + file_path)

if __name__ == "__main__":
    generate_coverage_report()