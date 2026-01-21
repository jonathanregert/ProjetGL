package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;


/**
 * Variable declaration
 *
 * @author gl42
 * @date 01/01/2026
 */
public abstract class AbstractDeclMethod extends Tree {
    
    /**
     * Implements non-terminal "decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv 
     *   its "parentEnvironment" corresponds to the "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to 
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to 
     *      the synthetized attribute
     * @param currentClass 
     *          corresponds to the "class" attribute (null in the main bloc).
     */    
    protected abstract void verifyDeclMethod(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError;

    protected abstract void codeGenDeclMethod(DecacCompiler compiler);

    protected abstract AbstractIdentifier getMethodName();

    protected abstract void verifyMethodBody(DecacCompiler compiler, 
        EnvironmentExp envExp, // L'environnement global
        ClassDefinition currentClass, 
        Type returnType)
            throws ContextualError;

    public abstract void codeGenByteDeclMethod(DecacCompiler compiler, String ownerClassName);
}

