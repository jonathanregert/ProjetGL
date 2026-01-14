#! /bin/sh
# Auteur : gl42
# Tests de génération BYTECODE JVM (Jasmin)
# Étape C – sans objet

set -e

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/main/bin:"$PATH"

DECAC=decac
JASMIN_JAR=./jasmin/jasmin.jar
JAVA=java

CODEGEN_DIR="src/test/deca/codegen/BYTE"
OUT_DIR="./jasmin/output"

mkdir -p "$OUT_DIR"

echo "===================================="
echo " Tests BYTECODE JVM (Jasmin)"
echo "===================================="

#######################################
# TESTS VALIDES
#######################################

echo
echo "Tests VALIDES"

find "$CODEGEN_DIR/valid" -name "*.deca" | sort | while read testfile; do
    echo
    echo "Test valide attendu : $testfile"

    # 1. Compilation Deca → .j
    if ! $DECAC --byte "$testfile" > /dev/null 2>&1; then
        echo "Erreur à la compilation Deca"
        exit 1
    fi

    jfile="${testfile%.deca}.j"

    if [ ! -f "$jfile" ]; then
        echo "Fichier .j non généré"
        exit 1
    fi

    # 2. Compilation Jasmin vers .class
    if ! $JAVA -jar "$JASMIN_JAR" -d "$OUT_DIR" "$jfile" > /dev/null 2>&1; then
        echo "Erreur Jasmin"
        exit 1
    fi

    classname=Main

    # 3. Exécution Java
    if ! $JAVA -cp "$OUT_DIR" "$classname" > /dev/null 2>&1; then
        echo "Erreur à l'exécution JVM"
        exit 1
    fi

    echo "Succès attendu"
done

# java -cp ./jasmin/output/ Main

#######################################
# TESTS INVALIDES
#######################################

echo
echo "Tests INVALIDES"

find "$CODEGEN_DIR/invalid" -name "*.deca" | sort | while read testfile; do
    echo
    echo "Test invalide attendu : $testfile"

    if $DECAC --byte "$testfile" > /dev/null 2>&1; then
        jfile="${testfile%.deca}.j"
        classname=$(basename "$jfile" .j)

        if $JAVA -jar "$JASMIN_JAR" -d "$OUT_DIR" "$jfile" > /dev/null 2>&1; then
            if $JAVA -cp "$OUT_DIR" "$classname" > /dev/null 2>&1; then
                echo "Succès inattendu"
                exit 1
            else
                echo "Échec attendu à l'exécution JVM"
            fi
        else
            echo "Échec attendu à la compilation Jasmin"
        fi
    else
        echo "Échec attendu à la compilation Deca"
    fi
done

echo
echo "===================================="
echo " Tous les tests BYTE sont passés"
echo "===================================="
exit 0
