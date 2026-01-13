package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.SEQ;
import fr.ensimag.ima.pseudocode.instructions.SLT;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "==";
    }

    @Override
    protected void codeGenSet(DecacCompiler compiler, GPRegister target){
        compiler.addInstruction(new SEQ(target));
    }

    @Override
    public int getPriorite() { return 50; }

    @Override
    protected void codeGenByteExpr(DecacCompiler compiler) {
        getLeftOperand().codeGenByteExpr(compiler);
        getRightOperand().codeGenByteExpr(compiler);
        compiler.getByteManager().emitCmp("eq"); // laisse 0 ou 1
    }

    @Override
    protected void emitIfCmpTrue(DecacCompiler compiler, String trueLabel) {
        compiler.getByteManager().emitIfCmp("eq", trueLabel);
    }

}
