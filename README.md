# Compilateur DECAC - Équipe GL42 (Ensimag)

## Présentation du projet
Ce projet consiste en la réalisation d'un compilateur complet pour le langage **Deca**, un langage objet simplifié inspiré de Java. Le compilateur, nommé `decac`, traduit le code source Deca en langage assembleur pour la machine abstraite **IMA** (Interactive Machine Architecture)

L'outil gère les concepts fondamentaux de la programmation orientée objet : encapsulation, héritage simple et polymorphisme via une table des méthodes virtuelles (VTable).

## Architecture du compilateur
Le processus de compilation est divisé en quatre phases principales

1. **Analyse Lexicale (Lexer)** : Découpage du texte source en lexèmes avec la bibliothèque ANTLR
2. **Analyse Syntaxique (Parser)** : Vérification de la structure grammaticale et construction de l'Arbre Syntaxique Abstrait (AST)
3. **Analyse Contextuelle (Verify)** : Vérification des types, gestion des portées et décoration de l'arbre.
4. **Génération de Code (CodeGen)** : Gestion de la mémoire (pile et tas) et traduction en instructions assembleur IMA.

## Installation et Configuration

### Pré-requis
* **Java** : JDK version 17 ou supérieure (testé sous JDK 23).
* **Maven** : Version 3.6 ou supérieure pour la gestion des dépendances.
* **IMA** : L'interpréteur de la machine abstraite doit être installé et accessible dans le `PATH`.

### Compilation
Pour générer l'exécutable, lancez la commande suivante à la racine du projet

```bash
mvn clean install
```

### Guide d'Utilisation
La syntaxe générale est : decac [options] <fichier_source.deca>.

#### Options principales

-b : Affiche la bannière de l'équipe.

-p : Arrête le compilateur après l'analyse syntaxique et affiche la décompilation.

-v : Arrête le compilateur après l'analyse contextuelle (vérification des types).

-n : Désactive les tests de débordement à l'exécution pour un code plus rapide.

-r X : Limite le nombre de registres banalisés utilisables entre 4 et 16. 

-P : Active la compilation parallèle pour traiter plusieurs fichiers simultanément.
