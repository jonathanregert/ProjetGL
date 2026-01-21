package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
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
        if (addr == null){
            throw new IllegalStateException("codeGenExpr a renvoyé null pour " + getLeftOperand());
        }
        compiler.addInstruction(new STORE(target, addr));
    }

    @Override
    protected void codeGenByteExpr(DecacCompiler compiler) {
        if (getLeftOperand() instanceof Identifier) {
            Identifier id = (Identifier) getLeftOperand();
            if (id.getDefinition().isField()) {
                // Cas : champ = expr (implicite this.champ = expr)
                FieldDefinition fd = id.getFieldDefinition();
                String owner = fr.ensimag.deca.codegen.ByteManager.toInternalClassName(
                    fd.getContainingClass().getType().getName().getName()
                );
                String name = id.getName().getName();
                String desc = fr.ensimag.deca.codegen.ByteManager.typeToDescriptor(fd.getType());

                compiler.getByteManager().emitALoad(0);         // empiler this
                getRightOperand().codeGenByteExpr(compiler);    // empiler la valeur (val)
                
                compiler.getByteManager().emitDUP_X1();         // -> val, this, val
                compiler.getByteManager().emitPutField(owner, name, desc); // consomme this, val -> reste val
            } else {
                getRightOperand().codeGenByteExpr(compiler);
                VariableDefinition def = id.getVariableDefinition();
                int slot = compiler.getLocalSlot(def);

                compiler.getByteManager().emitDUP(); // On garde la valeur pour l'expression
                
                if (getType().isInt() || getType().isBoolean()) {
                    compiler.getByteManager().emitIStore(slot);
                } else if (getType().isFloat()) {
                    compiler.getByteManager().emitFStore(slot);
                } else {
                    compiler.getByteManager().emitAStore(slot);
                }
            }
            return;
        }

        if (getLeftOperand() instanceof Selection) {
            Selection sel = (Selection) getLeftOperand();
            FieldDefinition fd = sel.getField().getFieldDefinition();
            String owner = fr.ensimag.deca.codegen.ByteManager.toInternalClassName(
                fd.getContainingClass().getType().getName().getName()
            );
            String fname = sel.getField().getName().getName();
            String desc = fr.ensimag.deca.codegen.ByteManager.typeToDescriptor(fd.getType());

            // obj then value
            sel.getObject().codeGenByteExpr(compiler);   // obj
            getRightOperand().codeGenByteExpr(compiler); // obj val

            // Duplicate value and reorder to leave value on stack after putfield
            compiler.getByteManager().emitDUP_X1();      // val obj val
            compiler.getByteManager().getInstructions().add("swap"); // obj val val
            compiler.getByteManager().emitPutField(owner, fname, desc); // consumes obj,val, leaves val
            return;
        }
    }

    @Override
    protected void codeGenByte(DecacCompiler compiler) {
        if (getLeftOperand() instanceof Identifier) {
            Identifier id = (Identifier) getLeftOperand();
            if (id.getDefinition().isField()) {
                // Cas : champ = expr (instruction)
                FieldDefinition fd = id.getFieldDefinition();
                String owner = fr.ensimag.deca.codegen.ByteManager.toInternalClassName(
                    fd.getContainingClass().getType().getName().getName()
                );
                String name = id.getName().getName();
                String desc = fr.ensimag.deca.codegen.ByteManager.typeToDescriptor(fd.getType());

                compiler.getByteManager().emitALoad(0);         // this
                getRightOperand().codeGenByteExpr(compiler);    // valeur
                compiler.getByteManager().emitPutField(owner, name, desc); // affectation
            } else {
                // Cas : variable = expr (instruction)
                getRightOperand().codeGenByteExpr(compiler);
                ExpDefinition def = id.getExpDefinition(); //param ou variable
                int slot = compiler.getLocalSlot(def);

                if (getType().isInt() || getType().isBoolean()) {
                    compiler.getByteManager().emitIStore(slot);
                } else if (getType().isFloat()) {
                    compiler.getByteManager().emitFStore(slot);
                } else {
                    compiler.getByteManager().emitAStore(slot);
                }
            }
            return;
        }

        if (getLeftOperand() instanceof Identifier) {
            getRightOperand().codeGenByteExpr(compiler);

            VariableDefinition def =
                ((Identifier) getLeftOperand()).getVariableDefinition();

            int slot = compiler.getLocalSlot(def);

            if (getType().isInt() || getType().isBoolean()) {
                compiler.getByteManager().emitIStore(slot);
            } else if (getType().isFloat()) {
                compiler.getByteManager().emitFStore(slot);
            } else {
                compiler.getByteManager().emitAStore(slot);
            }
            return;
        }

        if (getLeftOperand() instanceof Selection) {
            Selection sel = (Selection) getLeftOperand();
            FieldDefinition fd = sel.getField().getFieldDefinition();
            String owner = fr.ensimag.deca.codegen.ByteManager.toInternalClassName(
                fd.getContainingClass().getType().getName().getName()
            );
            String fname = sel.getField().getName().getName();
            String desc = fr.ensimag.deca.codegen.ByteManager.typeToDescriptor(fd.getType());

            sel.getObject().codeGenByteExpr(compiler);   // obj
            getRightOperand().codeGenByteExpr(compiler); // obj val
            compiler.getByteManager().emitPutField(owner, fname, desc); // consumes obj,val
            return;
        }
    }

}
