package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeGauche = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type typeDroite = getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        if (!typeGauche.isBoolean() || !typeDroite.isBoolean()) {
            throw new ContextualError("L'opérateur logique ne s'applique qu'aux booléens.", getLocation());
        }
        Type boolType = compiler.environmentType.BOOLEAN;
        this.setType(boolType);
        return boolType;
    }

    @Override
    public void codeGenExpr(DecacCompiler compiler, GPRegister target){
        int id = compiler.getLabelId();
        Label shortCircuitLabel = new Label("bool_sc_" + id);
        Label endLabel = new Label("bool_end_" + id);

        getLeftOperand().codeGenExpr(compiler, target);

        compiler.addInstruction(new CMP(new ImmediateInteger(0), target));

        compiler.addInstruction(getChildBranch(shortCircuitLabel));

        getRightOperand().codeGenExpr(compiler, target);
        compiler.addInstruction(new BRA(endLabel));

        compiler.addLabel(shortCircuitLabel);
        compiler.addInstruction(new LOAD(new ImmediateInteger(getShortCircuitValue()), target));

        compiler.addLabel(endLabel);

    }

    protected abstract Instruction getChildBranch(Label shortCircuitLabel);

    protected abstract int getShortCircuitValue();

    @Override
    protected void codeGenByteExpr(DecacCompiler compiler) {
        String scLabel = compiler.getByteManager().newLabel();
        String endLabel = compiler.getByteManager().newLabel();

        // gauche
        getLeftOperand().codeGenByteExpr(compiler);
        emitShortCircuitBranch(compiler, scLabel);

        // droite
        getRightOperand().codeGenByteExpr(compiler);
        compiler.getByteManager().emitGoto(endLabel);

        // short-circuit
        compiler.getByteManager().emitLabel(scLabel);
        compiler.getByteManager().emitLDC(getShortCircuitValue());

        compiler.getByteManager().emitLabel(endLabel);
    }
    protected abstract void emitShortCircuitBranch(
        DecacCompiler compiler,
        String shortCircuitLabel
    );
}
