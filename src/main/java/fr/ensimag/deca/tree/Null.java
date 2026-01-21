package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * Littéral null.
 */
public class Null extends AbstractExpr {

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type type = compiler.environmentType.NULL;
        this.setType(type);
        return type;
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        compiler.addInstruction(new LOAD(new NullOperand(), target));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("null");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // Feuille de l'arbre
    }

    @Override
    protected void prettyPrintChildren(java.io.PrintStream s, String prefix) {
        // Feuille de l'arbre
    }

    @Override
    protected void codeGenByteExpr(DecacCompiler compiler) {
        compiler.getByteManager().emitAConstNull();
    }
}