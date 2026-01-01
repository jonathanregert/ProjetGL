package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.CMP;
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


    @Override
    protected void codeGenInst(DecacCompiler compiler) {

        // 1. Évaluer l'opérande gauche → R1
        getLeftOperand().codeGenInst(compiler);

        // 2. Sauvegarder le résultat
        compiler.addInstruction(new PUSH(Register.R1));

        // 3. Évaluer l'opérande droite → R1
        getRightOperand().codeGenInst(compiler);

        // 4. Récupérer l'opérande gauche dans R2
        compiler.addInstruction(new POP(Register.getR(2)));

        // 5. Comparer : R2 ? R1   (ATTENTION à l'ordre)
        compiler.addInstruction(new CMP(Register.R1, Register.getR(2)));

        /*
        * Après CMP :
        *   R2 - R1 est évalué
        *   les flags sont positionnés
        */

        // 6. Transformer le résultat de comparaison en booléen (0 ou 1)
        if (this instanceof Lower) {
            compiler.addInstruction(new SLT(Register.R1));
        } else if (this instanceof LowerOrEqual) {
            compiler.addInstruction(new SLE(Register.R1));
        } else if (this instanceof Greater) {
            compiler.addInstruction(new SGT(Register.R1));
        } else if (this instanceof GreaterOrEqual) {
            compiler.addInstruction(new SGE(Register.R1));
        } else if (this instanceof Equals) {
            compiler.addInstruction(new SEQ(Register.R1));
        } else if (this instanceof NotEquals) {
            compiler.addInstruction(new SNE(Register.R1));
        } else {
            throw new UnsupportedOperationException("Comparateur non supporté");
        }
    }
    
}