package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int id = compiler.getLabelId();
        Label trueLabel = new Label("or_true_" + id);
        Label endLabel = new Label("or_end_" + id);

        // gauche
        getLeftOperand().codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, Register.R1));
        compiler.addInstruction(new BNE(trueLabel));

        // droite
        getRightOperand().codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, Register.R1));
        compiler.addInstruction(new BNE(trueLabel));

        // false
        compiler.addInstruction(new LOAD(0, Register.R1));
        compiler.addInstruction(new BRA(endLabel));

        // true
        compiler.addLabel(trueLabel);
        compiler.addInstruction(new LOAD(1, Register.R1));

        compiler.addLabel(endLabel);
    }


}
