package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.SEQ;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type operandType = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!operandType.isBoolean()) {
            throw new ContextualError("L'opérateur '!' ne s'applique qu'aux booléens.", getLocation());
        }
        Type boolType = compiler.environmentType.BOOLEAN;
        this.setType(boolType);
        return boolType;
    }


    @Override
    protected String getOperatorName() {
        return "!";
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        getOperand().codeGenExpr(compiler, target);
        compiler.addInstruction(new CMP(new ImmediateInteger(0), target));
        compiler.addInstruction(new SEQ(target));
    }

    @Override
    public int getPriorite() {
        return 90;
    }

    @Override
    protected void codeGenByteExpr(DecacCompiler compiler) {
        String t = compiler.getByteManager().newLabel();
        String f = compiler.getByteManager().newLabel();
        String end = compiler.getByteManager().newLabel();

        codeGenByteCond(compiler, t, f);

        compiler.getByteManager().emitLabel(t);
        compiler.getByteManager().emitLDC(1);
        compiler.getByteManager().emitGoto(end);

        compiler.getByteManager().emitLabel(f);
        compiler.getByteManager().emitLDC(0);

        compiler.getByteManager().emitLabel(end);
    }


    @Override
    protected void codeGenByteCond(
        DecacCompiler compiler,
        String trueLabel,
        String falseLabel
    ) {
        // inversion logique
        getOperand().codeGenByteCond(compiler, falseLabel, trueLabel);
    }


}
