package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.REM;




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
    protected void codeGenOperator(DecacCompiler compiler, int leftRegister) {
        compiler.addInstruction(
            new MUL(Register.getR(leftRegister + 1), Register.getR(leftRegister))
        );
    }


}
