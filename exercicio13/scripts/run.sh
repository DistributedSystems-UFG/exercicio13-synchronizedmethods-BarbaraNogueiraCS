#!/usr/bin/env bash
set -euo pipefail

# Garante uma localidade UTF-8 para lidar com caminhos como "Área de Trabalho".
export LANG="${LANG:-C.UTF-8}"
export LC_ALL="${LC_ALL:-C.UTF-8}"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SRC_DIR="$ROOT_DIR/src/main/java"
OUT_DIR="$ROOT_DIR/out"

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Usa array para preservar caminhos com espaços, por exemplo: "Área de Trabalho".
mapfile -t JAVA_FILES < <(find "$SRC_DIR" -name "*.java" | sort)

if [ "${#JAVA_FILES[@]}" -eq 0 ]; then
  echo "Nenhum arquivo .java encontrado em: $SRC_DIR" >&2
  exit 1
fi

javac -encoding UTF-8 -d "$OUT_DIR" "${JAVA_FILES[@]}"
java -cp "$OUT_DIR" br.ufg.scd.exercicio13.CounterExperiment "$@"
