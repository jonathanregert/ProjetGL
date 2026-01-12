package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;

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
        if (!(super.getLeftOperand() instanceof AbstractLValue)) {
            throw new IllegalStateException("Left operand n'est pas une lvalue");
        }
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

        // verif partie gauche :
        if (getLeftOperand() instanceof AbstractIdentifier) {
            AbstractIdentifier id = (AbstractIdentifier) getLeftOperand();

            if (id.getDefinition().isField()) {
                FieldDefinition fieldDef = (FieldDefinition) id.getDefinition();
                if (fieldDef.isFinal()) {
                throw new ContextualError("Affectation à un champ final '" 
                    + id.getName() + "' interdite", getLocation());
            }
            }
        if (getLeftOperand() instanceof Selection) {
            Selection sel = (Selection) getLeftOperand();
            Definition def = sel.getField().getDefinition();
            if (def != null && def.isField()) {
                FieldDefinition fieldDef = (FieldDefinition) def;
                if (fieldDef.isFinal()) {
                    throw new ContextualError("Affectation à un champ final '"
                            + sel.getField().getName() + "' interdite", getLocation());
                }
            }
        }
    }

        // partie droite
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
    public void decompile(IndentPrintStream s) {
        getLeftOperand().decompile(s);
        s.print(" = ");
        getRightOperand().decompile(s);
    }

    @Override
    public int getPriorite() { return 10; }

    @Override
    protected boolean isRightAssociative() {
        return true;
    }


    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        getRightOperand().codeGenExpr(compiler, target);
        DAddr addr = getLeftOperand().codeGenAddr(compiler);
        compiler.addInstruction(new STORE(target, addr));
    }
}
