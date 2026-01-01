package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Register;
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
        // Cas d'erreur
        else {
            throw new ContextualError("Opération arithmétique impossible entre " + typeGauche + " et " + typeDroite,
                    this.getLocation());
        }
            }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getLeftOperand().codeGenInst(compiler);
        compiler.addInstruction(new PUSH(Register.R1));
        getRightOperand().codeGenInst(compiler);
        compiler.addInstruction(new POP(Register.getR(2)));

        codeGenOperator(compiler);
    }

    protected abstract void codeGenOperator(DecacCompiler compiler);

}
