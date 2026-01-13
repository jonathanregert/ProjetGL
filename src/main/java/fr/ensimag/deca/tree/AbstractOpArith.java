package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl42
 * @date 01/01/2026
 */

public abstract class AbstractOpArith extends AbstractBinaryExpr {
    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeGauche = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type typeDroite = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        // int et int -> int
        if (typeGauche.isInt() && typeDroite.isInt()) {
            this.setType(compiler.environmentType.INT);
            return typeGauche;
        }

        // float et float -> float
        else if (typeGauche.isFloat() && typeDroite.isFloat()) {
        this.setType(typeGauche);
        return typeGauche;
        }

        // float et int -> float
        else if (typeGauche.isFloat() && typeDroite.isInt()) {
        ConvFloat conv = new ConvFloat(this.getRightOperand()); // on transforme l'opérande droite : int -> float
        conv.verifyExpr(compiler, localEnv, currentClass);
        this.setRightOperand(conv);
        this.setType(typeGauche);
        return typeGauche;
        }

        // int et float -> float
        // symetrique du cas précédent
        else if (typeGauche.isInt() && typeDroite.isFloat()) {
        ConvFloat conv = new ConvFloat(this.getLeftOperand());
        conv.verifyExpr(compiler, localEnv, currentClass); // Décore le ConvFloat
        this.setLeftOperand(conv);
        this.setType(typeDroite);
        return typeDroite;
        }
        else {
            throw new ContextualError("Opération arithmétique impossible entre " + typeGauche + " et " + typeDroite,
                    this.getLocation());
        }
    }
    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        // gauche dans target
        getLeftOperand().codeGenExpr(compiler, target);
        //droite dans un nouveau registre
        GPRegister rRight = compiler.getRegAllocator().alloc();
        if (rRight != null){
            getRightOperand().codeGenExpr(compiler, rRight);
            codeGenOperator(compiler, rRight, target);
            compiler.getRegAllocator().free(rRight);
            return;
        }
        compiler.addInstruction(new PUSH(target));
        if (compiler.getStackManager() != null){
            compiler.getStackManager().useTemp(1);
        }
        // droite dans target
        getRightOperand().codeGenExpr(compiler, target);
        compiler.addInstruction(new POP(Register.getR(2)));
        if (compiler.getStackManager() != null){
            compiler.getStackManager().releaseTemp(1);
        }
        codeGenOperator(compiler, target, Register.getR(2));
        compiler.addInstruction(new LOAD(Register.getR(2), target));
        
    }
    protected abstract void codeGenOperator(DecacCompiler compiler, GPRegister rRight, GPRegister rLeft);

    @Override
    protected void codeGenByteExpr(DecacCompiler compiler) {
        getLeftOperand().codeGenByteExpr(compiler);
        getRightOperand().codeGenByteExpr(compiler);
        codeGenByteOperator(compiler);
    }
    protected abstract void codeGenByteOperator(DecacCompiler compiler);

}
