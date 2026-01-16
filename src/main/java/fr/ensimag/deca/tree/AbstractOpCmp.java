package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
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

        if (!(typeGauche.isInt() || typeGauche.isFloat()) || !(typeDroite.isInt() || typeDroite.isFloat())) {
        throw new ContextualError("Les comparaisons d'inégalité ne sont possibles qu'entre nombres.", getLocation());
        }
        
        if (typeGauche.isFloat() && typeDroite.isInt()) {
            setRightOperand(new ConvFloat(getRightOperand()));
            getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        } else if (typeGauche.isInt() && typeDroite.isFloat()) {
            setLeftOperand(new ConvFloat(getLeftOperand()));
            getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        }
        else if (!typeGauche.sameType(typeDroite)) {
            throw new ContextualError("Types incompatibles pour comparaison: "
                    + typeGauche.getName().getName() + " et "
                    + typeDroite.getName().getName(),
                    this.getLocation());
        }
        Type bool = compiler.environmentType.BOOLEAN;
        this.setType(bool);
        return bool;
    }


    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        getLeftOperand().codeGenExpr(compiler, target);

        GPRegister rRight = compiler.getRegAllocator().alloc();

        if (rRight != null){
            getRightOperand().codeGenExpr(compiler, rRight);
            compiler.addInstruction(new CMP(rRight, target));
            codeGenSet(compiler, target);
            compiler.getRegAllocator().free(rRight);
            return;
        }

        compiler.addInstruction(new PUSH(target));
        compiler.getStackManager().useTemp(1);

        getRightOperand().codeGenExpr(compiler, target);

        compiler.addInstruction(new POP(Register.getR(2)));
        compiler.getStackManager().releaseTemp(1);

        compiler.addInstruction(new CMP(target, Register.getR(2)));

        codeGenSet(compiler, target);
    }

    /**
     * Génère l'instruction qui transforme les flags en booléen dans target.
     * @param compiler
     * @param target
     */
    protected abstract void codeGenSet(DecacCompiler compiler, GPRegister target);

    @Override
    protected boolean needsParensForChild(AbstractExpr child) {
        return child instanceof AbstractOpCmp;
    }

    
}