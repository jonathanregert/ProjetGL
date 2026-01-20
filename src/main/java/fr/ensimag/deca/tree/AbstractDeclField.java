package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
/**
 * Field declaration
 *
 * @author gl42
 * @date 03/01/2026
 */

public abstract class AbstractDeclField extends Tree {
    
    /**
     * Implements non-terminal "decl_field" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv 
     *   its "parentEnvironment" corresponds to the "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to 
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to 
     *      the synthetized attribute
     * @param currentClass 
     *          corresponds to the "class" attribute.
     */    
    protected abstract void verifyDeclField(DecacCompiler compiler,
            ClassDefinition currentClass)
            throws ContextualError;

    protected abstract void codeGenDeclField(DecacCompiler compiler);

    protected abstract AbstractIdentifier getFieldName();

    protected abstract void codeGenInitField(DecacCompiler compiler, ClassDefinition currentClass);

    protected abstract void verifyFieldInitialization(DecacCompiler compiler,
            ClassDefinition currentClass) throws ContextualError;
}
