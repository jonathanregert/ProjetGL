#! /bin/sh

# Auteur : gl42
# Tests syntaxiques globaux – étape A
# Vérifie les fichiers .deca de src/test/deca/syntax/

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/main/bin:"$PATH"

SYNTAX_DIR="src/test/deca/syntax"

echo "Tests syntaxiques"

# Tests INVALIDES

echo "Tests INVALIDES"

find "$SYNTAX_DIR/invalid" -name "*.deca" | sort | while read testfile; do
    echo "Test invalide attendu : $testfile"

    if decac -p "$testfile" 2>&1 | grep -q "$testfile:[0-9]"; then
        echo "Echec attendu"
    else
        echo "Succes inattendu"
        exit 1
    fi
done

# Tests VALIDES

echo "Tests VALIDES"

find "$SYNTAX_DIR/valid" -name "*.deca" | sort | while read testfile; do
    echo "Test valide attendu : $testfile"

    if decac -p "$testfile" 2>&1 | grep -q "$testfile:[0-9]"; then
        echo "Erreur inattendue"
        exit 1
    else
        echo "Succes attendu"
    fi
done

echo "Tous les tests syntaxiques sont passes"
exit 0