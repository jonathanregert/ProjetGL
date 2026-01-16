package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
/**
 * 
 * @author gl42
 * @date 01/01/2026
 */
public class ListInst extends TreeList<AbstractInst> {

    /**
     * Implements non-terminal "list_inst" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv corresponds to "env_exp" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     * @param returnType
     *          corresponds to "return" attribute (void in the main bloc).
     */    
    public void verifyListInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        for (AbstractInst i : getList()) { //parcours de chaque instruction
            i.verifyInst(compiler, localEnv, currentClass, returnType); //vérification de chaque instruction
        }
    }

    public void codeGenListInst(DecacCompiler compiler) {
        for (AbstractInst i : getList()) {
            i.codeGenInst(compiler);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractInst i : getList()) {
            i.decompileInst(s);
            s.println();
        }
    }
    public boolean containsReturn() {
    for (AbstractInst inst : getList()) {
        if (inst instanceof Return) {
            return true;
        }
        if (inst instanceof IfThenElse) {
            IfThenElse ifInst = (IfThenElse) inst;
            if (ifInst.getThenBranch().containsReturn() &&
                (ifInst.getElseBranch() != null && ifInst.getElseBranch().containsReturn())) {
                return true;
            }
        } 
    }
    return false;
    }
}
