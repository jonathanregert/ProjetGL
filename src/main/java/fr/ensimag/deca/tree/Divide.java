package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.QUO;
import fr.ensimag.ima.pseudocode.instructions.REM;


/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "/";
    }

    @Override
    protected void codeGenOperator(DecacCompiler compiler, GPRegister rRight, GPRegister rLeft) {
        if (getType().isInt()){
            compiler.getErrorManager().genCheckIntDivByZero(compiler, rRight);
            compiler.addInstruction(new QUO(rRight, rLeft));
        } else {
            compiler.getErrorManager().genCheckFloatDivByZero(compiler, rRight);
            compiler.addInstruction(new DIV(rRight, rLeft));
        }
        compiler.getErrorManager().genCheckOverflow(compiler);
    }

    @Override
    public int getPriorite() { return 80; }

    @Override
    protected boolean isLeftAssociative() {
        return true;
    }

    @Override
    protected void codeGenByteOperator(DecacCompiler compiler) {
        if (getType().isInt()) {
            compiler.getByteManager().emitIDIV();
        } else if (getType().isFloat()) {
            compiler.getByteManager().emitFDIV();
        } else {
            throw new UnsupportedOperationException(
                "Divide non supporté pour " + getType()
            );
        }
    }

}
