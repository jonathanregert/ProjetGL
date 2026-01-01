package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Register;
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
    protected void codeGenOperator(DecacCompiler compiler) {
        compiler.addInstruction(
            new MUL(Register.getR(2), Register.R1)
        );
    }


}
