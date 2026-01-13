package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

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

}
