# Projet Génie Logiciel, Ensimag.
gl42, 01/01/2026.

mvn clean
mvn test -Djacoco.skip=false -Dexec.executable="./src/test/script/all-context-tests.sh"
mvn jacoco:restore-instrumented-classes -Djacoco.skip=false
mvn jacoco:report -Djacoco.skip=false
firefox target/site/jacoco/index.html