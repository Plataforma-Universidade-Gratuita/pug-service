#!/usr/bin/env bash
set -euo pipefail

OUT="project-tree.txt"
# names to ignore at top-level (exact match)
IGNORE_TOP="(.git|.idea|target|node_modules|.mvn|build|.DS_Store|project-tree.txt)"

# produce a tree-like listing using find + awk
# - exclude common ignored dirs
find . \
  \( -path "./.git" -o -path "./.idea" -o -path "./target" -o -path "./node_modules" -o -path "./.mvn" -o -path "./build" -o -name ".DS_Store" -o -name "project-tree.txt" \) -prune -o -print \
| sed 's#^\./##' \
| awk -F'/' '
  BEGIN{
    OFS="/";
    print "./"
  }
  {
    n = NF;
    # build prefix
    prefix="";
    for(i=1;i<n;i++){
      parent = $i;
      # store counts per level to determine pipe/space - use array counts[level]
      counts[i]++;
    }
    # accumulate path to determine if entry is last among siblings
    path = $0;
    arr[path]=1;
    list[n,path]=$0;
    items[n, ++idx[n]] = $0;
  }
  END{
    # simpler second pass: build tree by iterating sorted entries
    PROCINFO["sorted_in"] = "@ind_str_asc";
    # collect all entries again using getline from find output is complex in awk end; instead, re-run find
  }'

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
      if [ "$dir" = "." ] && [[ "$name" =~ ^(.git|.idea|target|node_modules|.mvn|build|.DS_Store|project-tree.txt)$ ]]; then
        continue
      fi
      generate "$e" "$newprefix"
    fi
  done
}

# write header and run
{
  echo "./"
  generate "." ""
} > "$OUT"

echo "Wrote $OUT"
