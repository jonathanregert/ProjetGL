package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    protected Instruction getChildBranch(Label shortCircuitLabel) {
        return new BEQ(shortCircuitLabel);
    }

    @Override
    protected int getShortCircuitValue(){
        return 0;
    }

    @Override
    public int getPriorite() { return 40; }

    @Override
    protected boolean isLeftAssociative() { return true; }

    @Override
    protected boolean isRightAssociative() { return true; }

}
