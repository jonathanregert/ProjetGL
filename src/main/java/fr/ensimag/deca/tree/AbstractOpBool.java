package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeGauche = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type typeDroite = getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        if (!typeGauche.isBoolean() || !typeDroite.isBoolean()) {
            throw new ContextualError("L'opérateur logique ne s'applique qu'aux booléens.", getLocation());
        }
        Type boolType = compiler.environmentType.BOOLEAN;
        this.setType(boolType);
        return boolType;
    }

    @Override
    public void codeGenInst(DecacCompiler compiler)
    {
        int id = compiler.getLabelId();
        Label firstLabel = new Label("and_false_" + id);
        Label secondLabel = new Label("and_end_" + id);

        getLeftOperand().codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, Register.R1));
        compiler.addInstruction(getChildLabel(firstLabel));

        getRightOperand().codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, Register.R1));
        compiler.addInstruction(getChildLabel(firstLabel));

        compiler.addInstruction(new LOAD(1, Register.R1));
        compiler.addInstruction(new BRA(secondLabel));

        compiler.addLabel(firstLabel);
        compiler.addInstruction(new LOAD(0, Register.R1));

        compiler.addLabel(secondLabel);
    }

    protected abstract Instruction getChildLabel(Label label);

}
