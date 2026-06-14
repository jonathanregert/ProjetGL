# DECAC - Compilateur Deca

Compilateur pour **Deca**, un langage objet simplifie inspire de Java, realise dans le cadre du Projet Genie Logiciel de l'Ensimag. Le compilateur `decac` analyse un programme Deca, verifie sa coherence contextuelle, puis genere du code assembleur pour la machine abstraite **IMA**. Une extension permet aussi de produire du bytecode JVM via Jasmin.

## Fonctionnalites

- Analyse lexicale et syntaxique avec ANTLR.
- Construction et manipulation d'un arbre syntaxique abstrait.
- Verification contextuelle : types, portees, declarations, heritage, appels de methodes.
- Generation de code IMA avec gestion des registres, de la pile et des erreurs d'execution.
- Support des objets : classes, champs, methodes, heritage simple, polymorphisme.
- Extension bytecode JVM avec l'option `--byte`.
- Scripts de tests syntaxiques, contextuels, codegen, bytecode et analyse energetique.

## Technologies

- Java 21
- Maven 3.6.3+
- ANTLR 4.13.2
- JUnit, Mockito et JaCoCo
- IMA pour l'execution du code assembleur genere
- Jasmin pour l'assemblage du bytecode JVM

## Structure

```text
src/main/antlr4/      Grammaires Deca
src/main/java/        Code du compilateur
src/main/bin/         Script executable decac
src/test/             Tests Java, Deca et scripts de validation
docs/                 Documentation utilisateur et bilan
docker/               Environnement de developpement
jasmin/               Support pour l'extension bytecode
analyse_energetique/  Scripts et donnees d'analyse energetique
```

## Installation

Cloner le depot puis compiler le projet :

```bash
mvn clean package
```

Le script `decac` s'appuie sur les classes et le classpath generes par Maven.

```bash
./src/main/bin/decac -b
```

Pour les tests de generation et l'execution des fichiers `.ass`, l'interpreteur `ima` doit etre disponible dans le `PATH`.

## Utilisation

Compiler un fichier Deca vers IMA :

```bash
./src/main/bin/decac chemin/vers/programme.deca
```

Le compilateur produit un fichier `.ass` a cote du fichier source. Exemple avec un test fourni :

```bash
./src/main/bin/decac src/test/deca/context/valid/sansObjet/hello-world.deca
ima src/test/deca/context/valid/sansObjet/hello-world.ass
```

Options principales :

| Option | Description |
| --- | --- |
| `-b` | Affiche la banniere de l'equipe. |
| `-p` | S'arrete apres l'analyse syntaxique et affiche la decompilation. |
| `-v` | S'arrete apres la verification contextuelle. |
| `-n` | Desactive certains controles d'execution. |
| `-r X` | Limite les registres banalisables, avec `4 <= X <= 16`. |
| `-d` | Active les traces de debug, cumulable jusqu'au niveau trace. |
| `-P` | Compile plusieurs fichiers en parallele. |
| `--byte` | Genere du bytecode JVM au format Jasmin. |

## Extension Bytecode JVM

L'option `--byte` genere un fichier `.j` Jasmin :

```bash
./src/main/bin/decac --byte src/test/deca/codegen/BYTE/valid/add.deca
java -jar jasmin/jasmin.jar src/test/deca/codegen/BYTE/valid/add.j
```

Les scripts de tests bytecode se trouvent dans `src/test/script/`, notamment :

```bash
./src/test/script/all-byte-tests.sh
```

## Tests

Quelques suites utiles :

```bash
./src/test/script/all-syntax-tests.sh
./src/test/script/all-context-tests-sansObjet.sh
./src/test/script/all-context-tests-avecObjet.sh
./src/test/script/all-codegen-tests.sh
./src/test/script/all-byte-tests.sh
```

Une suite rapide est egalement disponible :

```bash
./src/test/script/fast-all-tests.sh
```

Les tests de codegen et certains tests bytecode supposent que `ima`, `java` et les dependances associees sont accessibles depuis l'environnement courant.

## Analyse energetique

Le dossier `analyse_energetique/` contient des scripts pour mesurer le processus de fabrication et estimer le cout d'execution de programmes IMA :

```bash
sh analyse_energetique/scripts/run_energy_commands.sh
python3 analyse_energetique/scripts/ima_estimate_cycles.py fichier.ass
```

## Documentation

- `docs/Manuel-Utilisateur.pdf` : manuel utilisateur.
- `docs/Bilan_Equipe.pdf` : bilan du projet.
- `planning/` : planning previsionnel et realisation.

## Auteurs

Projet realise par l'equipe **GL42** dans le cadre du Projet Genie Logiciel de l'Ensimag.
