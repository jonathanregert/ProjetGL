package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.SLE;
/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class LowerOrEqual extends AbstractOpIneq {
    public LowerOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "<=";
    }

    @Override
    protected void codeGenSet(DecacCompiler compiler, GPRegister target){
        compiler.addInstruction(new SLE(target));
    }

    @Override
    public int getPriorite() { return 60; }

    @Override
    protected void emitIfCmpTrue(DecacCompiler compiler, String trueLabel) {
        compiler.getByteManager().emitIfCmp("le", trueLabel);
    }

}
