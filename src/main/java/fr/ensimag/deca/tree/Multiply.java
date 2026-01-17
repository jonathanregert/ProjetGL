package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.MUL;

/**
 * @author gl42
 * @date 01/01/2026
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "*";
    }

    @Override
    protected void codeGenOperator(DecacCompiler compiler, GPRegister rRight, GPRegister rLeft) {
        compiler.addInstruction(new MUL(rRight, rLeft));
        compiler.getErrorManager().genCheckOverflow(compiler);
    }

    @Override
    public int getPriorite() { return 80; }

    @Override
    protected boolean isLeftAssociative() { return true; }

    @Override
    protected boolean isRightAssociative() { return true; }

}
