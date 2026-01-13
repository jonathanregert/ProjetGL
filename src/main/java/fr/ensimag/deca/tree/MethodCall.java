package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.codegen.ErrorManager.RuntimeError;
import fr.ensimag.deca.codegen.RegAllocator;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

import org.apache.commons.lang.Validate;

import static org.mockito.ArgumentMatchers.*;

import java.io.PrintStream;
import java.util.List;

public  class MethodCall extends AbstractExpr {

    private final AbstractExpr object;
    private final AbstractIdentifier method;
    private final ListExpr arguments;

    public MethodCall(AbstractExpr object,
                      AbstractIdentifier method,
                      ListExpr arguments) {
        Validate.notNull(method);
        Validate.notNull(arguments);
        this.object = object;
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {

        Type objType = object.verifyExpr(compiler, localEnv, currentClass);
        if (!objType.isClass()) {
            throw new ContextualError("Appel de méthode sur un type non-objet", getLocation());
        }

        ClassDefinition classDef = ((ClassType) objType).getDefinition();

        Definition def = classDef.getMembers().get(method.getName());
        if( def == null){throw new ContextualError(
                    "méthode inconnue" + method.getName() ,
                    method.getLocation());
                }
        MethodDefinition mdef = def == null ? null
                : def.asMethodDefinition(method.getName() + " n'est pas une méthode", method.getLocation());
        
        if (mdef == null) {
            throw new ContextualError("Méthode " + method.getName() + " non définie dans la classe " + classDef.getType().getName(), method.getLocation());
        } // si methode nexiste pas

        Signature sig = mdef.getSignature();

        if (arguments.size() != sig.size()) {
            throw new ContextualError(
                    "Nombre d'arguments incorrect pour " + method.getName()
                            + " : attendu " + sig.size() + ", obtenu " + arguments.size(),
                    getLocation()
            );
        }

        List<AbstractExpr> args = arguments.getList();
        for (int i = 0; i < args.size(); i++) {
            Type expected = sig.paramNumber(i);
            AbstractExpr checked = args.get(i).verifyRValue(compiler, localEnv, currentClass, expected);
            args.set(i, checked);
        }

        method.setDefinition(mdef);
        setType(mdef.getType());
        return getType();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        object.decompile(s);
        s.print(".");
        method.decompile(s);
        s.print("(");
        arguments.decompile(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        object.iter(f);
        method.iter(f);
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        object.prettyPrint(s, prefix, false);
        method.prettyPrint(s, prefix, false);
        arguments.prettyPrint(s, prefix, true);
    }
    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {

        object.codeGenExpr(compiler, Register.R1);

        Label nullDeref = compiler.getErrorManager().label(RuntimeError.NULL_DEREFERENCE);
        compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.R1));
        compiler.addInstruction(new BEQ(nullDeref));

        List<AbstractExpr> args = arguments.getList();
        for (int i = args.size() - 1; i >= 0; i--) {
            args.get(i).codeGenExpr(compiler, Register.R0);
            compiler.addInstruction(new PUSH(Register.R0));
            compiler.getStackManager().useTemp(1);
        }

        compiler.addInstruction(new PUSH(Register.R1));
        compiler.getStackManager().useTemp(1);

        MethodDefinition mdef = method.getMethodDefinition();
        int idx = mdef.getIndex();
        int slot = 1 + idx;

        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R1), Register.R0));

        compiler.addInstruction(new LOAD(new RegisterOffset(slot, Register.R0), Register.R0));

        compiler.getStackManager().useTemp(2);
        compiler.addInstruction(new BSR(Register.R0));
        compiler.getStackManager().releaseTemp(2);

        // 7) Dépiler args + this
        int toPop = args.size() + 1;
        compiler.addInstruction(new ADDSP(new ImmediateInteger(toPop)));
        compiler.getStackManager().releaseTemp(toPop);

        // 8) Résultat : R0 -> target
        if (target.getNumber() != Register.R0.getNumber()) {
            compiler.addInstruction(new LOAD(Register.R0, target));
        }
    }
}
