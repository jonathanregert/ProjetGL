#!/bin/bash

DECAC_REF="./src/main/bin/decac"

# 2. Dossier contenant tes tests
TEST_DIR="src/test/deca/codegen/valid/avecObjet"

# Vérification de l'existence du compilateur de référence
if [ ! -f "$DECAC_REF" ]; then
    echo "Erreur : Compilateur de référence introuvable à : $DECAC_REF"
    exit 1
fi

echo "--- Génération des fichiers .res via le compilateur de référence ---"

for deca_file in "$TEST_DIR"/*.deca; do
    base="${deca_file%.*}"
    filename=$(basename "$deca_file")

    echo -n "Traitement de $filename... "

    # a. Compiler le fichier .deca en .ass avec la RÉFÉRENCE
    $DECAC_REF "$deca_file" > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        # b. Exécuter le .ass avec ima et sauvegarder dans .res
        # On utilise 'ima' qui doit être dans ton PATH
        ima "$base.ass" > "$base.res"
        echo "OK (Généré)"
    else
        echo "ERREUR (La référence n'a pas pu compiler ce fichier)"
    fi

    # Nettoyage du fichier .ass de référence
    rm -f "$base.ass"
done

echo "--- Terminé ---"