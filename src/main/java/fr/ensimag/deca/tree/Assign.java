package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl42
 * @date 01/01/2026
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // Partie gauche doit être une lvalue (p 58)
        if (!(getLeftOperand() instanceof AbstractLValue)) {
        throw new ContextualError("La partie gauche d'une affectation doit être une lvalue.",
                getLeftOperand().getLocation());
        }

        Type typeGauche = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);

        AbstractExpr rhs = getRightOperand().verifyRValue(compiler, localEnv, currentClass, typeGauche);
        setRightOperand(rhs);

        this.setType(typeGauche);
        return typeGauche;
    }


    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        getRightOperand().codeGenExpr(compiler, target);
        DAddr addr = getLeftOperand().codeGenAddr(compiler);
        compiler.addInstruction(new STORE(target, addr));
    }
}
