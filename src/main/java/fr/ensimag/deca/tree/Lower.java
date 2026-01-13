package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.SLT;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class Lower extends AbstractOpIneq {

    public Lower(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "<";
    }

    @Override
    protected void codeGenSet(DecacCompiler compiler, GPRegister target){
        compiler.addInstruction(new SLT(target));
    }

    @Override
    public int getPriorite() { return 60; }

    @Override
    protected void emitIfCmpTrue(DecacCompiler compiler, String trueLabel) {
        compiler.getByteManager().emitIfCmp("lt", trueLabel);
    }

}
