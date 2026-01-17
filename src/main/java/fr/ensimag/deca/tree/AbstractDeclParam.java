package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Variable declaration
 *
 * @author gl42
 * @date 01/01/2026
 */
public abstract class AbstractDeclParam extends Tree {
    
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
    protected abstract void verifyDeclParam(DecacCompiler compiler,
            EnvironmentExp localEnv)
            throws ContextualError;

    protected abstract Type verifyDeclParamType(DecacCompiler compiler) throws ContextualError;
    
}

