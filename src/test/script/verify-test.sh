#!/bin/bash

# Dossier contenant tes tests
TEST_DIR="src/test/deca/codegen/valid/avecObjet"

GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' 

echo -e "${BLUE}--- DÉBUT DE LA VÉRIFICATION DES TESTS ---${NC}"

for file in "$TEST_DIR"/*.deca; do
    filename=$(basename "$file")
    base="${file%.*}"
    
    # On vérifie si un fichier .res existe pour ce test
    if [ ! -f "$base.res" ]; then
        echo -e "Test $filename : ${BLUE}Ignoré (pas de .res)${NC}"
        continue
    fi

    echo -n "Test $filename : "

    # 1. Compilation (.deca -> .ass)
    ./src/main/bin/decac "$file" 2>/dev/null
    if [ $? -ne 0 ]; then
        echo -e "${RED}ERREUR COMPILATION${NC}"
        continue
    fi

    # 2. Exécution IMA et redirection vers .actual
    ima "$base.ass" > "$base.actual" 2>&1

    # 3. Comparaison avec le .res
    # On utilise 'diff' pour comparer les fichiers
    # -w ignore les espaces blancs et les retours à la ligne vides
    diff_output=$(diff -w "$base.actual" "$base.res")

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}PASS${NC}"
        # On peut supprimer les fichiers temporaires si c'est OK
        rm "$base.actual" "$base.ass"
    else
        echo -e "${RED}FAIL (Différence trouvée)${NC}"
        echo "--- Détails du diff ---"
        echo "$diff_output"
        echo "-----------------------"
    fi
done

echo -e "${BLUE}--- FIN DES TESTS ---${NC}"