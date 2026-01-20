package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BNE;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    @Override
    protected Instruction getChildBranch(Label shortCircuitLabel) {
        return new BNE(shortCircuitLabel);
    }

    @Override
    protected int getShortCircuitValue(){
        return 1;
    }

    @Override
    public int getPriorite() { return 30; }

    @Override
    protected boolean isLeftAssociative() { return true; }

    @Override
    protected boolean isRightAssociative() { return true; }

    @Override
    protected void emitShortCircuitBranch(DecacCompiler compiler, String label) {
        compiler.getByteManager().emitIfNe(label);
    }

    @Override
    protected void codeGenByteCond(
        DecacCompiler compiler,
        String trueLabel,
        String falseLabel
    ) {
        String mid = compiler.getByteManager().newLabel();

        // si gauche == true → trueLabel
        getLeftOperand().codeGenByteCond(compiler, trueLabel, mid);

        compiler.getByteManager().emitLabel(mid);

        // sinon évaluer droite
        getRightOperand().codeGenByteCond(compiler, trueLabel, falseLabel);
    }

}
