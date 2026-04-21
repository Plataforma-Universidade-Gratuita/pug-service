#!/usr/bin/env bash
set -euo pipefail

# Argumentos: 1 = Diretório de Saída, 2 = Sufixo (opcional)
OUT_DIR="${1:-$PWD}"
SUFFIX="${2:-}"

mkdir -p "$OUT_DIR"

# Define o nome base
if [ -n "$SUFFIX" ]; then
    BASE_NAME="project_context_${SUFFIX}"
else
    CURRENT_DIR_NAME=$(basename "$PWD")
    BASE_NAME="project_context_${CURRENT_DIR_NAME}"
fi

EXTENSION=".txt"

# Lógica para evitar sobrescrever e adicionar (1), (2), etc.
COUNTER=0
OUTPUT_FILE="$OUT_DIR/${BASE_NAME}${EXTENSION}"

while [ -f "$OUTPUT_FILE" ]; do
    ((COUNTER++))
    OUTPUT_FILE="$OUT_DIR/${BASE_NAME}(${COUNTER})${EXTENSION}"
done

SCRIPT_NAME=$(basename "$0")

echo "Scanning directory: $PWD"
echo "Output will be saved to: $OUTPUT_FILE"

{
    echo "PROJECT CONTEXT DUMP"
    echo "Base: $BASE_NAME"
    echo "Root Path: $PWD"
    echo "================================================================================"
    echo ""
} > "$OUTPUT_FILE"

# Find e processamento
find . \
    \( -name ".git" -o -name ".idea" -o -name ".vscode" -o -name "node_modules" -o -name "dist" -o -name "build" -o -name "coverage" -o -name "target" -o -name ".mvn" -o -name "venv" -o -name ".settings" -o -name "__pycache__" \) -prune \
    -o -type f \
    -not -name "$(basename "$OUTPUT_FILE")" \
    -not -name "$SCRIPT_NAME" \
    -not -name "*.class" -not -name "*.jar" -not -name "*.war" \
    -not -name "*.exe" -not -name "*.dll" -not -name "*.zip" \
    -not -name "*.pdf" -not -name "*.png" -not -name "*.jpg" -not -name "*.ico" \
    -print0 | while IFS= read -r -d $'\0' file; do

    if grep -Iq . "$file" || [ ! -s "$file" ]; then
        CLEAN_NAME=$(echo "$file" | sed 's|^\./||')
        {
            echo ""
            echo "================================================================================"
            echo "FILE: $CLEAN_NAME"
            echo "================================================================================"
            cat "$file"
            echo ""
        } >> "$OUTPUT_FILE"
        echo "Included: $CLEAN_NAME"
    fi
done

echo "File created: $OUTPUT_FILE"