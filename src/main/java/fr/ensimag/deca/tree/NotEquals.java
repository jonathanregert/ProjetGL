package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.SLT;
import fr.ensimag.ima.pseudocode.instructions.SNE;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class NotEquals extends AbstractOpExactCmp {

    public NotEquals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "!=";
    }

    @Override
    protected void codeGenSet(DecacCompiler compiler, GPRegister target){
        compiler.addInstruction(new SNE(target));
    }

    @Override
    public int getPriorite() { return 50; }

}
