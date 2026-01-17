package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 *
 * @author gl42
 * @date 01/01/2026
 */
public abstract class AbstractOpExactCmp extends AbstractOpCmp {

    public AbstractOpExactCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type typeGauche = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type typeDroite = getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        Type boolType = compiler.environmentType.BOOLEAN;

        if (typeGauche.isBoolean() && typeDroite.isBoolean()) {
            this.setType(boolType);
            return boolType;
        }

        if ((typeGauche.isInt() || typeGauche.isFloat()) && (typeDroite.isInt() || typeDroite.isFloat())) {
            if (typeGauche.isFloat() && typeDroite.isInt()) {
                setRightOperand(new ConvFloat(getRightOperand()));
                getRightOperand().verifyExpr(compiler, localEnv, currentClass);
            } else if (typeGauche.isInt() && typeDroite.isFloat()) {
                setLeftOperand(new ConvFloat(getLeftOperand()));
                getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
            }
            this.setType(boolType);
            return boolType;
        }

        if ((typeGauche.isClass() || typeGauche.isNull()) && (typeDroite.isClass() || typeDroite.isNull())) {
            this.setType(boolType);
            return boolType;
        }

        throw new ContextualError("Types incompatibles pour la comparaison : " + typeGauche + " et " + typeDroite,
                getLocation());
    }
}
