package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;


/**
 * List of declarations (e.g. int x; float y,z).
 * 
 * @author gl42
 * @date 01/01/2026
 */
public class ListDeclParam extends TreeList<AbstractDeclParam> {
    @Override
    public void decompile(IndentPrintStream s) {
        boolean first = true;
        for (AbstractDeclParam decl : getList()) {
            if (!first) {
                s.print(", ");
            }
            decl.decompile(s);
            first = false;
        }
    }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv 
     *   its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to 
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to 
     *      the "env_exp_r" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     */ 

    public Signature verifyListDeclParam(DecacCompiler compiler) throws ContextualError {
    Signature sig = new Signature();
    for (AbstractDeclParam p : getList()) {
        // On vérifie le type du paramètre et on l'ajoute à la signature
        Type t = p.verifyDeclParamType(compiler);
        sig.add(t);
    }
    return sig;
    }
    

    
    public void verifyListDeclParam(DecacCompiler compiler, EnvironmentExp localEnv) throws ContextualError {
            for (AbstractDeclParam declParam : this.getList()) {
                declParam.verifyDeclParam(compiler, localEnv);
            }
        }
}
