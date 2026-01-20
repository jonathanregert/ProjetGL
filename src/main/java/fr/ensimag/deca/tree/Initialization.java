package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.STORE;


/**
 * @author gl42
 * @date 01/01/2026
 */
public class Initialization extends AbstractInitialization {

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
            
            this.expression = this.expression.verifyRValue(compiler, localEnv, currentClass, t);
            
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" = ");
        expression.decompile(s);

    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }

    public void codeGenValue(DecacCompiler compiler, GPRegister target) {
        expression.codeGenExpr(compiler, target);
    }

    @Override
    public void codeGenInitialization(DecacCompiler compiler, DAddr target) {
        Validate.notNull(target, "Initialization target address is null");

        // Évaluer l'expression dans un registre alloué
        var r = compiler.getRegAllocator().alloc();
        if (r == null) {
            // si vraiment plus de registres (rare ici), on peut utiliser R0 scratch
            r = Register.R0;
        }
        expression.codeGenExpr(compiler, r);

        // Stocker en mémoire
        compiler.addInstruction(new STORE(r, target));

        // Libérer si c’était un registre alloué
        if (!r.equals(Register.R0)) {
            compiler.getRegAllocator().free(r);
        }
    }
}
