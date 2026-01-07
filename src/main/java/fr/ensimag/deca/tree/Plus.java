package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;


/**
 * @author gl42
 * @date 01/01/2026
 */
public class Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected String getOperatorName() {
        return "+";
    }
    @Override
    protected void codeGenOperator(DecacCompiler compiler, GPRegister rRight, GPRegister rLeft) {
        compiler.addInstruction(new ADD(rRight, rLeft));
    }
}
