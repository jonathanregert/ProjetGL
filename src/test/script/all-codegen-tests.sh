#! /bin/sh

# Auteur : gl42
# Tests de generation de code – étape C
# Vérifie les fichiers .deca de src/test/deca/codegen/

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/main/bin:"$PATH"

CODEGEN_DIR="src/test/deca/codegen"

echo "Tests codegen"

# Tests INVALIDES

echo "Tests INVALIDES"

find "$CODEGEN_DIR/invalid/" -name "*.deca" | sort | while read testfile; do
    echo "Test invalide attendu : $testfile"

    if decac "$testfile" > /dev/null 2>&1; then
        assfile="${testfile%.deca}.ass"

        if ima "$assfile" > /dev/null 2>&1; then
            echo "Succes inattendu (execution)"
        else
            echo "Echec attendu a l'execution"
        fi
    else
        echo "Erreur inattendue a la compilation"
        exit 1
    fi
done

# Tests VALIDES

echo "Tests VALIDES"

find "$CODEGEN_DIR/valid/" -name "*.deca" | sort | while read testfile; do
    echo "Test valide attendu : $testfile"

    if decac "$testfile" > /dev/null 2>&1; then
        assfile="${testfile%.deca}.ass"

        if ima "$assfile" > /dev/null 2>&1; then
            echo "Succes attendu"
        else
            echo "Erreur a l'execution"
            exit 1
        fi
    else
        echo "Erreur a la compilation"
        exit 1
    fi
done

echo "Tous les tests codegen sont passes"
exit 0