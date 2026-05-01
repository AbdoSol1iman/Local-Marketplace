#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
ENDPOINT="${ENDPOINT:-/api/categories}"
REQUESTS="${REQUESTS:-100}"
P95_MAX_SECONDS="${P95_MAX_SECONDS:-0.300}"
AVG_MAX_SECONDS="${AVG_MAX_SECONDS:-0.150}"

tmp_dir="$(mktemp -d)"
raw_file="$tmp_dir/raw.txt"
sorted_file="$tmp_dir/sorted.txt"
trap 'rm -rf "$tmp_dir"' EXIT

target="${BASE_URL}${ENDPOINT}"

echo "Benchmark target: $target"
echo "Requests: $REQUESTS"

# Warm-up request
curl -sS -o /dev/null "$target"

for _ in $(seq 1 "$REQUESTS"); do
  curl -sS -o /dev/null -w '%{time_total}\n' "$target" >> "$raw_file"
done

sort -n "$raw_file" > "$sorted_file"

n="$(wc -l < "$sorted_file" | tr -d ' ')"
if [[ "$n" -eq 0 ]]; then
  echo "No latency samples collected."
  exit 1
fi

p50_idx=$(( (50 * n + 99) / 100 ))
p95_idx=$(( (95 * n + 99) / 100 ))
p99_idx=$(( (99 * n + 99) / 100 ))

min="$(sed -n '1p' "$sorted_file")"
p50="$(sed -n "${p50_idx}p" "$sorted_file")"
p95="$(sed -n "${p95_idx}p" "$sorted_file")"
p99="$(sed -n "${p99_idx}p" "$sorted_file")"
max="$(sed -n "${n}p" "$sorted_file")"
avg="$(awk '{s+=$1} END{printf "%.6f", s/NR}' "$raw_file")"

echo "count=$n avg=${avg}s min=${min}s p50=${p50}s p95=${p95}s p99=${p99}s max=${max}s"

if awk -v a="$avg" -v p="$p95" -v amax="$AVG_MAX_SECONDS" -v pmax="$P95_MAX_SECONDS" 'BEGIN {exit !(a<=amax && p<=pmax)}'; then
  echo "PASS: avg <= ${AVG_MAX_SECONDS}s and p95 <= ${P95_MAX_SECONDS}s"
else
  echo "FAIL: threshold exceeded (avg<=${AVG_MAX_SECONDS}s, p95<=${P95_MAX_SECONDS}s)"
  exit 2
fi
