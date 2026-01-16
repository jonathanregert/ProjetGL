#!/bin/bash

# Dossier contenant tes tests (à adapter si besoin)

cd "$(dirname "$0")"/../../.. || exit 1

TEST_DIR="src/test/deca/context/valid/avecObjet"

echo "---------------------------------------------"
echo "    EXÉCUTION DES TESTS DECA"
echo "---------------------------------------------"

# On boucle sur chaque fichier .deca
for file in "$TEST_DIR"/*.deca; do
    filename=$(basename "$file")
    base="${file%.*}"

    # 1. Compilation
    ./src/main/bin/decac "$file" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo "Fichier : $filename"
        echo -n "Sortie  : "
        
        # 2. Exécution et capture de la sortie
        sortie=$(ima "$base.ass")
        echo "$sortie"
        echo "---------------------------------------------"
    else
        echo "Fichier : $filename"
        echo "Sortie  : [ERREUR DE COMPILATION]"
        echo "---------------------------------------------"
    fi
done

# Nettoyage des fichiers .ass générés
rm -f "$TEST_DIR"/*.ass