#!/usr/bin/env bash
set -euo pipefail

OUT="project-tree.txt"

# 1. TREE GENERATION
# ---------------------------------------------------------
echo "Generating project tree..."

# fallback simpler working approach using recursion in shell
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
      # skip ignored top-level names when recursion depth is 1
      # (escaped dots in the regex to accurately match literal dots)
      if [ "$dir" = "." ] && [[ "$name" =~ ^(\.git|\.idea|target|node_modules|\.mvn|build|\.DS_Store|project-tree\.txt)$ ]]; then
        continue
      fi
      generate "$e" "$newprefix"
    fi
  done
}

# write header and run tree generation
{
  echo "./"
  generate "." ""
} > "$OUT"

echo "Wrote tree to $OUT"


# 2. RUN GENERATE-CONTEXT SCRIPTS
# ---------------------------------------------------------
# Loop over both target directories to avoid duplicated code
for target_dir in "src" "requests"; do
    echo ""
    echo "Searching for context generation scripts under ./$target_dir..."

    # Check if directory exists to prevent errors
    if [ -d "$target_dir" ]; then
        # Find files named 'generate-context.sh' inside the current target directory
        find "$target_dir" -type f -name "generate-context.sh" -print0 | while IFS= read -r -d $'\0' script_path; do

            script_dir=$(dirname "$script_path")
            script_name=$(basename "$script_path")

            echo ""
            echo "Found script: $script_path"
            echo "--------------------------------------------------"

            # Execute in a subshell (...) so it doesn't change the main script's working directory
            (
                cd "$script_dir" || exit
                echo "Running $script_name in $(pwd)..."

                # Ensure it is executable (optional but good practice)
                chmod +x "$script_name" 2>/dev/null || true

                # Run using bash explicitly
                bash "$script_name"
            )
            echo "--------------------------------------------------"
        done
    else
        echo "Directory './$target_dir' does not exist. Skipping script execution."
    fi
done

echo ""
echo "All operations completed."