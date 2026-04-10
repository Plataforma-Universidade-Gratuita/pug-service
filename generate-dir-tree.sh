#!/usr/bin/env bash
set -euo pipefail

# Determine absolute path to the root context directory
ROOT_DIR="$PWD"
CONTEXT_DIR="$ROOT_DIR/context"

# Create the context directory if it doesn't exist
mkdir -p "$CONTEXT_DIR"

OUT="$CONTEXT_DIR/project-tree.txt"

# 1. TREE GENERATION
echo "Generating project tree..."

generate() {
  local dir="$1"
  local prefix="$2"
  local items=()
  while IFS= read -r -d $'\0' entry; do
    items+=( "$entry" )
  done < <(find "$dir" -maxdepth 1 -mindepth 1 -print0 | sort -z)

  local total=${#items[@]}
  local i=0
  for e in "${items[@]}"; do
    i=$((i+1))
    local name
    name="$(basename "$e")"
    local isdir=0
    [ -d "$e" ] && isdir=1
    local connector="├── "
    local newprefix="${prefix}│   "
    if [ "$i" -eq "$total" ]; then
      connector="└── "
      newprefix="${prefix}    "
    fi
    printf "%s%s%s%s\n" "$prefix" "$connector" "$name" "$( [ $isdir -eq 1 ] && printf "/" )"
    if [ $isdir -eq 1 ]; then
      if [ "$dir" = "." ] && [[ "$name" =~ ^(\.git|\.idea|target|node_modules|\.mvn|build|\.DS_Store|project-tree\.txt|context)$ ]]; then
        continue
      fi
      generate "$e" "$newprefix"
    fi
  done
}

{
  echo "./"
  generate "." ""
} > "$OUT"

echo "Wrote tree to $OUT"

# 2. RUN GENERATE-CONTEXT SCRIPTS
for target_dir in "src" "requests" "academic" "geo"; do
    if [ -d "$target_dir" ]; then
        find "$target_dir" -type f -name "generate-context.sh" -print0 | while IFS= read -r -d $'\0' script_path; do
            script_dir=$(dirname "$script_path")
            script_name=$(basename "$script_path")

            # Gera um sufixo amigável baseado no caminho (ex: academic-domain-enums)
            DIR_SUFFIX=$(echo "$script_dir" | tr '/' '-')

            echo ""
            echo "Found script: $script_path"
            echo "--------------------------------------------------"
            (
                cd "$script_dir" || exit
                chmod +x "$script_name" 2>/dev/null || true
                bash "$script_name" "$CONTEXT_DIR" "$DIR_SUFFIX"
            )
            echo "--------------------------------------------------"
        done
    fi
done

echo ""
echo "All operations completed. Check the ./context folder for outputs."