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
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeGauche = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type typeDroite = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        // être de type int ou float 
        if (!(typeGauche.isInt() || typeGauche.isFloat()) || !(typeDroite.isInt() || typeDroite.isFloat())) {
        throw new ContextualError("Les comparaisons d'inégalité ne sont possibles qu'entre nombres.", getLocation());
        }

        // si meme type int ou float, ok, rien a faire
        
        if (typeGauche.isFloat() && typeDroite.isInt()) {
        setRightOperand(new ConvFloat(getRightOperand()));
        getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        } else if (typeGauche.isInt() && typeDroite.isFloat()) {
        setLeftOperand(new ConvFloat(getLeftOperand()));
        getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        }
        else if (!typeGauche.sameType(typeDroite)) {
            throw new ContextualError("Incompatible types for comparison: " 
                    + typeGauche.getName().getName() + " and " 
                    + typeDroite.getName().getName(), 
                    this.getLocation());
        }
        Type bool = compiler.environmentType.BOOLEAN;
        this.setType(bool);
        return bool;
    }
}