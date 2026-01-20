package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;


/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class This extends AbstractExpr {
    

    @Override
    public Type verifyExpr(DecacCompiler compiler,
                           EnvironmentExp localEnv,
                           ClassDefinition currentClass)
            throws ContextualError {

        if (currentClass == null) {
            throw new ContextualError(
                "Utilisation de this hors d'une classe",
                getLocation()
            );
        }

        Type t = currentClass.getType();
        setType(t);
        return t;
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        compiler.addInstruction(
            new LOAD( new RegisterOffset(-2, Register.LB), target));
    }
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("this");
    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }

    @Override
    protected void prettyPrintChildren(
            java.io.PrintStream s, String prefix) {
                
            }
}