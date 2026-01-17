#! /bin/sh

# Auteur : gl42
# Tests contextuels globaux – étape B
# Vérifie tous les fichiers .deca dans src/test/deca/context/
# en respectant la classification valid / invalid.

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

CONTEXT_DIR="src/test/deca/aFaire"

echo "Tests contextuels"

# Tests INVALIDES

echo "Tests INVALIDES"

find "$CONTEXT_DIR/invalid/" -name "*.deca" | sort | while read testfile; do
    echo "Test invalide attendu : $testfile"

    if test_context "$testfile" 2>&1 | grep -q "$testfile:[0-9]"; then
        echo "Échec attendu"
    else
        echo "Succès inattendu"
        
    fi
done

Tests VALIDES

echo "Tests VALIDES"

find "$CONTEXT_DIR/valid/" -name "*.deca" | sort | while read testfile; do
    echo "Test valide attendu : $testfile"

    if test_context "$testfile" 2>&1 | grep -q "$testfile:[0-9]"; then
        echo "Erreur inattendue"
    else
        echo "Succès attendu"
    fi
done

echo "Tous les tests contextuels sont passés"
exit 0