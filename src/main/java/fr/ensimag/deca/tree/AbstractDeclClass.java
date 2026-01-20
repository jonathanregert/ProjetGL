package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;

/**
 * Class declaration.
 *
 * @author gl42
 * @date 01/01/2026
 */
public abstract class AbstractDeclClass extends Tree {

    /**
     * Pass 1 of [SyntaxeContextuelle]. Verify that the class declaration is OK
     * without looking at its content.
     */
    protected abstract void verifyClass(DecacCompiler compiler)
            throws ContextualError;


    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members (fields and
     * methods) are OK, without looking at method body and field initialization.
     */
    protected abstract void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     */
    protected abstract void verifyClassBody(DecacCompiler compiler)
            throws ContextualError;


    public abstract AbstractIdentifier getName();

    /**
     * Prépare les tables de méthodes sans générer d'instructions exécutables.
     * 1. Pour chaque classe : calcule la taille de la table
     * 2. Réserve les cases mémoire en GB
     * 3. Mémorise l'adresse de base de la table
     * @param compiler
     */
    public abstract void codeGenMTable(DecacCompiler compiler);

    /**
     * Écrit les tables en mémoire
     * @param compiler
     */
    public abstract void codeGenBuildMTable(DecacCompiler compiler);

    /**
     * Génère le sous-programme init.A
     * @param compiler
     */
    public abstract void codeGenInit(DecacCompiler compiler);


    /**
     * Génère le code des méthodes de la classe
     * @param compiler
     */
    public abstract void codeGenMethods(DecacCompiler compiler);
}
