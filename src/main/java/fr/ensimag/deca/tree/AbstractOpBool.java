package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
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

}
