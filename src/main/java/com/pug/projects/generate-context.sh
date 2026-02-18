#!/usr/bin/env bash
set -euo pipefail

# 1. Determine Output Filename based on current directory name
CURRENT_DIR_NAME=$(basename "$PWD")
OUTPUT_FILE="project_context_${CURRENT_DIR_NAME}.txt"
SCRIPT_NAME=$(basename "$0")

# 2. Configuration: Directories to Ignore
# Using prune logic for find
IGNORE_DIRS="(.git|.idea|.vscode|node_modules|dist|build|coverage|target|.mvn|venv|.settings|__pycache__)"

# 3. Configuration: Extensions to Ignore
# We will filter these out inside the loop or via find logic
# Note: Common binaries are excluded via grep -I later as well

echo "Scanning directory: $PWD"
echo "Output will be saved to: $OUTPUT_FILE"

# Initialize the output file with a header
{
    echo "PROJECT CONTEXT DUMP"
    echo "Project Name: $CURRENT_DIR_NAME"
    echo "Root Path: $PWD"
    echo "================================================================================"
    echo ""
} > "$OUTPUT_FILE"

FILE_COUNT=0

# 4. Find and Process Files
# Explanation of find command:
# - path ... -prune: Skip ignored directories completely
# - type f: Only look for files
# - not name: Exclude the script itself and the output file
# - not name: Exclude specific binary extensions explicitly
find . \
    \( -name ".git" -o -name ".idea" -o -name ".vscode" -o -name "node_modules" -o -name "dist" -o -name "build" -o -name "coverage" -o -name "target" -o -name ".mvn" -o -name "venv" -o -name ".settings" -o -name "__pycache__" \) -prune \
    -o -type f \
    -not -name "$OUTPUT_FILE" \
    -not -name "$SCRIPT_NAME" \
    -not -name "*.class" \
    -not -name "*.jar" \
    -not -name "*.war" \
    -not -name "*.exe" \
    -not -name "*.dll" \
    -not -name "*.zip" \
    -not -name "*.pdf" \
    -not -name "*.png" \
    -not -name "*.jpg" \
    -not -name "*.ico" \
    -print0 | while IFS= read -r -d $'\0' file; do

    # 5. Binary Check
    # grep -I treats binary files as non-matches. 
    # If grep finds text (or empty), we process it. If it detects binary, we skip.
    # We check if file is text OR if file is empty (to include empty source files)
    if grep -Iq . "$file" || [ ! -s "$file" ]; then
        
        # Clean up the ./ prefix for readability
        CLEAN_NAME=$(echo "$file" | sed 's|^\./||')

        # Append to output file
        {
            echo ""
            echo "================================================================================"
            echo "FILE: $CLEAN_NAME"
            echo "================================================================================"
            cat "$file"
            echo "" # Ensure newline at end
        } >> "$OUTPUT_FILE"

        echo "Included: $CLEAN_NAME"
        ((FILE_COUNT++)) || true
    fi
done

echo ""
echo "Success! Processed context for $CURRENT_DIR_NAME."
echo "File created: $OUTPUT_FILE"