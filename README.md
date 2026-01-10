# Projet Génie Logiciel, Ensimag.
gl42, 01/01/2026.

mvn clean
mvn test -Djacoco.skip=false -Dexec.executable="./src/test/script/all-context-tests.sh"
mvn jacoco:restore-instrumented-classes -Djacoco.skip=false
mvn jacoco:report -Djacoco.skip=false
google-chrome target/site/jacoco/index.html

mvn verify -DskipTests -Dexec.executable="./src/test/script/all-context-tests.sh"

Actualiser :
Lancez la commande Maven standard : mvn test -Djacoco.skip=false.
./src/test/script/jacoco-report.sh
google-chrome target/site/jacoco/index.html



Instructions qui marchent pour tests deca:
mvn clean verify -Djacoco.skip=false -DskipTests -Dexec.executable="./src/test/script/all-context-tests.sh"
mvn jacoco:restore-instrumented-classes -Djacoco.skip=false
mvn jacoco:report -Djacoco.skip=false
google-chrome target/site/jacoco/index.html


