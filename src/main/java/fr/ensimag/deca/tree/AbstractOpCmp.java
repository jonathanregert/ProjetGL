package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.SEQ;
import fr.ensimag.ima.pseudocode.instructions.SGE;
import fr.ensimag.ima.pseudocode.instructions.SGT;
import fr.ensimag.ima.pseudocode.instructions.SLE;
import fr.ensimag.ima.pseudocode.instructions.SLT;
import fr.ensimag.ima.pseudocode.instructions.SNE;
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

    @Override
    protected void codeGenByteExpr(DecacCompiler compiler) {
        String trueLabel = compiler.getByteManager().newLabel();
        String falseLabel = compiler.getByteManager().newLabel();
        String endLabel = compiler.getByteManager().newLabel();

        // produit uniquement des branchements (ne laisse rien sur la pile)
        codeGenByteCond(compiler, trueLabel, falseLabel);

        // true -> 1
        compiler.getByteManager().emitLabel(trueLabel);
        compiler.getByteManager().emitLDC(1);
        compiler.getByteManager().emitGoto(endLabel);

        // false -> 0
        compiler.getByteManager().emitLabel(falseLabel);
        compiler.getByteManager().emitLDC(0);

        compiler.getByteManager().emitLabel(endLabel);
    }


    protected abstract void emitIfCmpTrue(
        DecacCompiler compiler,
        String trueLabel
    );


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

    @Override
    protected void codeGenByteCond(DecacCompiler compiler, String trueLabel, String falseLabel) {
        Type t = getLeftOperand().getType(); // après ConvFloat

        getLeftOperand().codeGenByteExpr(compiler);
        getRightOperand().codeGenByteExpr(compiler);

        if (t.isInt() || t.isBoolean()) {
            // int,int -> if_icmp*
            emitIfCmpTrue(compiler, trueLabel);
        } else if (t.isFloat()) {
            // float,float -> fcmpl ; if<cond> (sur int)
            compiler.getByteManager().emitFCMPL(); // (voir étape 3)
            compiler.getByteManager().emitIfCmpFloatTrue(getOperatorName(), trueLabel);
        } else {
            throw new UnsupportedOperationException("Comparaison JVM non supportée pour " + t);
        }

        compiler.getByteManager().emitGoto(falseLabel);
    }


    
}