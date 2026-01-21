#!/bin/bash

# Chemins vers les outils
DECAC=./src/main/bin/decac
JASMIN_JAR=./jasmin/jasmin.jar
TEST_DIR=src/test/deca/codegen/valid/sansObjet
OUTPUT_DIR=src/test/deca/codegen/BYTE/valid

# Couleurs pour l'affichage
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

mkdir -p "$OUTPUT_DIR"

echo "--------------------------------------------------"
echo "Lancement des tests BYTE (Sans Objet)"
echo "--------------------------------------------------"

for file in "$TEST_DIR"/*.deca; do
    filename=$(basename "$file" .deca)
    
    echo -n "Test $filename : "

    # 1. Compilation Deca -> Jasmin (.j)
    # On utilise l'option --byte définie dans votre projet
    $DECAC --byte "$file" > /dev/null 2>&1
    
    if [ ! -f "${TEST_DIR}/${filename}.j" ]; then
        echo -e "${RED}[ERREUR COMPILATION DECAC]${NC}"
        continue
    fi

    # Déplacer le fichier .j vers le dossier de sortie
    mv "${TEST_DIR}/${filename}.j" "$OUTPUT_DIR/"

    # 2. Assemblage Jasmin -> .class
    java -jar "$JASMIN_JAR" -d "$OUTPUT_DIR" "$OUTPUT_DIR/${filename}.j" > /dev/null 2>&1
    
    if [ ! -f "$OUTPUT_DIR/Main.class" ]; then
        echo -e "${RED}[ERREUR JASMIN]${NC}"
        continue
    fi

    # 3. Exécution JVM
    # Votre codeGenByte génère toujours une classe nommée "Main"
    output=$(java -cp "$OUTPUT_DIR" Main)
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}[OK]${NC}"
        echo "Sortie : $output"
    else
        echo -e "${RED}[ERREUR EXECUTION JVM]${NC}"
    fi

    # Nettoyage pour le test suivant
    rm -f "$OUTPUT_DIR/Main.class"
done