package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import java.io.PrintStream;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class ReadFloat extends AbstractReadExpr {

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
            Type type = compiler.environmentType.FLOAT;
            setType(type);
            return type;
            }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("readFloat()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addInstruction(new RFLOAT());
    }


    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        compiler.addInstruction(new RFLOAT());
        compiler.getErrorManager().genCheckReadFloat(compiler);
        if (!target.equals(Register.R1)){
            compiler.addInstruction(new LOAD(Register.R1, target));
        }
    }

    @Override
    protected void codeGenByteExpr(DecacCompiler compiler) {
        compiler.getByteManager().emitReadFloat();
    }

}
