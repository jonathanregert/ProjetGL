#!/bin/bash

# Chemins vers les outils
DECAC=./src/main/bin/decac
JASMIN_JAR=./jasmin/jasmin.jar
TEST_DIR=src/test/deca/syntax/valid/
OUTPUT_DIR=src/test/deca/syntax/BYTE/valid

# Option:
#   --stop-after-j : s'arrete apres generation des .j (pas de jasmin/java)
STOP_AFTER_J=0
if [ "${1:-}" = "--stop-after-j" ]; then
    STOP_AFTER_J=1
fi

# Couleurs pour l'affichage
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

mkdir -p "$OUTPUT_DIR"
# Nettoyage pour éviter les résidus d’anciennes compilations
rm -f "$OUTPUT_DIR"/*.j "$OUTPUT_DIR"/*.class

echo "--------------------------------------------------"
echo "Lancement des tests BYTE (Syntaxe - Avec Objet)"
echo "--------------------------------------------------"

for file in "$TEST_DIR"/*.deca; do
    filename=$(basename "$file" .deca)

    echo -n "Test $filename : "

    # Nettoyer les artefacts précédents pour éviter les conflits de Main/A/etc.
    rm -f "$OUTPUT_DIR"/*.class "$OUTPUT_DIR"/*.j

    # 0. Verification syntaxique uniquement (-p)
    $DECAC -p "$file" > /dev/null 2>&1
    if [ $? -ne 0 ]; then
        echo -e "${RED}[ERREUR SYNTAXE]${NC}"
        continue
    fi

    # 1. Compilation Deca -> Jasmin (.j)
    $DECAC --byte "$file" > /dev/null 2>&1

    if [ ! -f "${TEST_DIR}/${filename}.j" ]; then
        echo -e "${RED}[ERREUR COMPILATION DECAC]${NC}"
        continue
    fi

    # Déplacer toutes les classes générées (.j) vers le dossier de sortie
    for gen in "$TEST_DIR"/*.j; do
        [ -f "$gen" ] || continue
        mv "$gen" "$OUTPUT_DIR/"
    done

    if [ $STOP_AFTER_J -eq 1 ]; then
        echo -e "${GREEN}[OK SYNTAXE + J]${NC}"
        continue
    fi

    # 2. Assemblage Jasmin -> .class (toutes les .j générées, car il peut y avoir des dépendances)
    for jfile in "$OUTPUT_DIR"/*.j; do
        java -jar "$JASMIN_JAR" -d "$OUTPUT_DIR" "$jfile" > /dev/null 2>&1
    done

    if [ ! -f "$OUTPUT_DIR/Main.class" ]; then
        echo -e "${RED}[ERREUR JASMIN]${NC}"
        continue
    fi

    # 3. Exécution JVM
    output=$(java -cp "$OUTPUT_DIR" Main)

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}[OK]${NC}"
        echo "Sortie : $output"
    else
        echo -e "${RED}[ERREUR EXECUTION JVM]${NC}"
    fi

    # Nettoyage pour le test suivant
    rm -f "$OUTPUT_DIR"/*.class

done
