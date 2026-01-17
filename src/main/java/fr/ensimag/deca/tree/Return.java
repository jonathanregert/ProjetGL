package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;


/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class Return extends AbstractInst {
    private AbstractExpr expr;

    public AbstractExpr getExpr() {
        return expr;
    }

    public Return(AbstractExpr expr) {
        Validate.notNull(expr);
        this.expr = expr;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        expr.codeGenExpr(compiler);
        compiler.addInstruction(new LOAD(Register.R1, Register.R0));
        compiler.addInstruction(new BRA(compiler.getCurrentMethodEndLabel()));
        
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
            
                if (returnType == null) {
            throw new ContextualError(
                "Instruction return hors d'une méthode",
                getLocation()
            );
        }
        // Vérification de la condition
        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);
        if (!exprType.equals(returnType)) {
    throw new ContextualError(
        "Type retourné " + exprType +
        " incompatible avec le type de retour " + returnType,
        expr.getLocation()
    );
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        expr.decompile(s);
        s.print(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, true);
    }
}