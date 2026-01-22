#!/bin/sh

set -u

ROOT_DIR=$(cd "$(dirname "$0")/../.." && pwd)
cd "$ROOT_DIR" || exit 1

# Save a full copy of the console output (stdout+stderr)
RESULTS_DIR="$ROOT_DIR/analyse_energetique/data"
RESULTS_FILE="$RESULTS_DIR/results.log"
mkdir -p "$RESULTS_DIR"
: > "$RESULTS_FILE"

OS=$(uname -s)
TIME_BIN=/usr/bin/time

if [ ! -x "$TIME_BIN" ]; then
  echo "Error: /usr/bin/time not found" >&2
  exit 2
fi

# Use raw /usr/bin/time output.
if [ "$OS" = "Darwin" ]; then
  TIME_FLAGS="-l"
else
  TIME_FLAGS="-v"
fi

time_cmd() {
  # Duplicate to console and to results.log
  # Note: stdout/stderr of the timed command are preserved.
  "$TIME_BIN" $TIME_FLAGS "$@" 2>&1 | tee -a "$RESULTS_FILE"
}

banner() {
  echo | tee -a "$RESULTS_FILE"
  echo "============================================================" | tee -a "$RESULTS_FILE"
  echo "$1" | tee -a "$RESULTS_FILE"
  echo "============================================================" | tee -a "$RESULTS_FILE"
}

# ------------------------------------------------------------
# 1) Processus de fabrication (compile + tests)
# ------------------------------------------------------------

banner "1) Build compiler: mvn compile"
time_cmd mvn -q compile || true

banner "2) Tests: all-tests.sh"
time_cmd sh -c "./src/test/script/all-tests.sh" || true

banner "3) Tests: all-syntax-tests.sh"
time_cmd sh -c "./src/test/script/all-syntax-tests.sh" || true

banner "4) Tests: all-context-tests-sansObjet.sh"
time_cmd sh -c "./src/test/script/all-context-tests-sansObjet.sh" || true

banner "5) Tests: all-context-tests-avecObjet.sh"
time_cmd sh -c "./src/test/script/all-context-tests-avecObjet.sh" || true

banner "6) Tests: all-codegen-tests-sansObjet.sh"
time_cmd sh -c "./src/test/script/all-codegen-tests.sh" || true

banner "7) Tests: all-codegen-tests-avecObjet.sh"
time_cmd sh -c "./src/test/script/all-codegen-tests.sh" || true

banner "8) Tests: execute_test_valid_sansObjet.sh"
time_cmd sh -c "./src/test/script/execute_test_valid_sansObjet.sh" || true

banner "9) Tests: execute_test_valid_avecObjet.sh"
time_cmd sh -c "./src/test/script/execute_test_valid_avecObjet.sh" || true

banner "10) Tests: test-byte-sansObjet.sh"
time_cmd sh -c "./src/test/script/test-byte-sansObjet.sh" || true

banner "11) Tests: test-byte-avecObjet.sh"
time_cmd sh -c "./src/test/script/test-byte-avecObjet.sh" || true

banner "12) Tests: all-byte-tests.sh"
time_cmd sh -c "./src/test/script/all-byte-tests.sh" || true

banner "13) Tests: test-byte-context-avecObjet.sh"
time_cmd sh -c "./src/test/script/test-byte-context-avecObjet.sh" || true

banner "14) Tests: test-byte-context-sansObjet.sh"
time_cmd sh -c "./src/test/script/test-byte-context-sansObjet.sh" || true

banner "15) Tests: test-byte-syntax.sh"
time_cmd sh -c "./src/test/script/test-byte-syntax.sh" || true

# ------------------------------------------------------------
# 2) Code produit IMA: compare normal vs -n via ima -s
# ------------------------------------------------------------

DECAC=./src/main/bin/decac

IMA=ima

IMA_BENCH_1=src/test/deca/context/valid/sansObjet/hello-world.deca
IMA_BENCH_2=src/test/deca/context/valid/sansObjet/while.deca
IMA_BENCH_3=src/test/deca/context/valid/avecObjet/method_body_with_control_ok.deca

for f in "$IMA_BENCH_1" "$IMA_BENCH_2" "$IMA_BENCH_3"; do
  if [ ! -f "$f" ]; then
    continue
  fi

  banner "IMA: compile + run (-s) for $f (checks)"
  time_cmd "$DECAC" "$f" || true
  ass="${f%.deca}.ass"
  if [ -n "$IMA" ] && [ -f "$ass" ]; then
    time_cmd "$IMA" -s "$ass" || true
  else
    echo "IMA not available or .ass missing; skip execution" | tee -a "$RESULTS_FILE"
  fi
  rm -f "$ass" 2>/dev/null || true

  banner "IMA: compile + run (-s) for $f (nocheck: -n)"
  time_cmd "$DECAC" -n "$f" || true
  ass="${f%.deca}.ass"
  if [ -n "$IMA" ] && [ -f "$ass" ]; then
    time_cmd "$IMA" -s "$ass" || true
  else
    echo "IMA not available or .ass missing; skip execution" | tee -a "$RESULTS_FILE"
  fi
  rm -f "$ass" 2>/dev/null || true

done

# ------------------------------------------------------------
# 3) Extension BYTE/JVM: decac --byte -> jasmin -> java
# ------------------------------------------------------------

JASMIN_JAR=./jasmin/jasmin.jar
JAVA=java

BYTE_BENCH_1=src/test/deca/codegen/BYTE/valid/add.deca
BYTE_BENCH_2=src/test/deca/codegen/BYTE/valid/AssignAndWhile.deca
BYTE_BENCH_3=src/test/deca/codegen/BYTE/valid/Control.deca

for f in "$BYTE_BENCH_1" "$BYTE_BENCH_2" "$BYTE_BENCH_3"; do
  if [ ! -f "$f" ]; then
    continue
  fi

  banner "BYTE: decac --byte + jasmin + java for $f"

  tmp=$(mktemp -d)
  out="$tmp/out"
  mkdir -p "$out"
  base=$(basename "$f" .deca)
  cp "$f" "$tmp/$base.deca"

  echo "-- Step A: decac --byte" | tee -a "$RESULTS_FILE"
  time_cmd "$DECAC" --byte "$tmp/$base.deca" || true

  echo "-- Step B: jasmin assemble (*.j -> .class)" | tee -a "$RESULTS_FILE"
  # shellcheck disable=SC2086
  time_cmd sh -c "$JAVA -jar \"$JASMIN_JAR\" -d \"$out\" \"$tmp\"/*.j" || true

  echo "-- Step C: java run Main" | tee -a "$RESULTS_FILE"
  time_cmd "$JAVA" -cp "$out" Main || true

  rm -rf "$tmp" 2>/dev/null || true

done

banner "Done"
