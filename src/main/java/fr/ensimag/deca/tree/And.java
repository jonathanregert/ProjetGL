package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int id = compiler.getLabelId();
        Label falseLabel = new Label("and_false_" + id);
        Label endLabel = new Label("and_end_" + id);

        // gauche
        getLeftOperand().codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, Register.R1));
        compiler.addInstruction(new BEQ(falseLabel));

        // droite
        getRightOperand().codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, Register.R1));
        compiler.addInstruction(new BEQ(falseLabel));

        // true
        compiler.addInstruction(new LOAD(1, Register.R1));
        compiler.addInstruction(new BRA(endLabel));

        // false
        compiler.addLabel(falseLabel);
        compiler.addInstruction(new LOAD(0, Register.R1));

        compiler.addLabel(endLabel);
    }


}
