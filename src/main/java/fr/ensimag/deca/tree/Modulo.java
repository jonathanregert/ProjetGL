package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeGauche = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type typeDroite = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!typeGauche.isInt() || !typeDroite.isInt()) {
            throw new ContextualError("L'opérateur modulo ne s'applique qu'aux entiers.", getLocation());
        }
        this.setType(typeGauche);
        return typeGauche;
    }


    @Override
    protected String getOperatorName() {
        return "%";
    }

    @Override
    protected void codeGenOperator(DecacCompiler compiler, GPRegister rRight, GPRegister rLeft) {
        compiler.getErrorManager().genCheckIntModByZero(compiler, rRight);
        compiler.addInstruction(new REM(rRight, rLeft));
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
            compiler.getByteManager().emitIREM();
        } else {
            throw new UnsupportedOperationException(
                "Modulo non supporté pour " + getType()
            );
        }
    }


}
