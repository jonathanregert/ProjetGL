#!/bin/bash

# On se place à la racine du projet si le script est lancé depuis src/test/script
cd "$(dirname "$0")/../../.."

echo "=========================================================="
echo "      LANCEMENT DE LA SUITE DE TESTS COMPLÈTE             "
echo "=========================================================="

# 1. Nettoyage et préparation de l'agent
echo "[1/4] Nettoyage et préparation de l'agent JaCoCo..."
mvn clean test-compile org.jacoco:jacoco-maven-plugin:prepare-agent -Djacoco.skip=false

# 2. Exécution des différents modules de tests
# On utilise 'append=true' implicitement via Maven pour cumuler les résultats
echo "[2/4] Exécution des tests de SYNTAXE..."
./src/test/script/all-syntax-tests.sh

echo "[3/4] Exécution des tests de CONTEXTE..."
./src/test/script/all-context-tests-avecObjet.sh
./src/test/script/all-context-tests-sansObjet.sh

echo "[4/4] Exécution des tests de CODEGEN (Génération de code)..."
./src/test/script/all-codegen-tests-avecObjet.sh
./src/test/script/all-codegen-tests-sansObjet.sh


echo "[4/4] Exécution des tests"

./src/test/script/execute_test_valid_avecObjet.sh
./src/test/script/execute_test_valid_sansObjet.sh


# 3. Finalisation JaCoCo
echo "----------------------------------------------------------"
echo "Génération du rapport de couverture global..."
mvn jacoco:restore-instrumented-classes jacoco:report -Djacoco.skip=false

echo "=========================================================="
echo "TESTS TERMINÉS !"
echo "Rapport global : target/site/jacoco/index.html"
echo "=========================================================="

google-chrome target/site/jacoco/index.html
