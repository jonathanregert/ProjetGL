package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * @author gl42
 * @date 01/01/2026
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type operandType = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!operandType.isInt() && !operandType.isFloat()) {
            throw new ContextualError("L'opérateur unaire '-' ne s'applique qu'aux entiers et flottants.", getLocation());
        }
        this.setType(operandType);
        return operandType;
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }

}
