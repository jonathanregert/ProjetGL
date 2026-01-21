package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.codegen.ErrorManager.RuntimeError;
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
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

import org.apache.commons.lang.Validate;

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

        int i = 0;
        for (AbstractExpr arg : arguments.getList()) {
        Type targetType = sig.paramNumber(i);
        AbstractExpr newArg = arg.verifyRValue(compiler, localEnv, currentClass, targetType);
        i++;
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

        List<AbstractExpr> args = arguments.getList();
        int nArgs = args.size();
        int nTotal = 1 + nArgs;

        Label nullDeref = compiler.getErrorManager().label(RuntimeError.NULL_DEREFERENCE);

        compiler.addInstruction(new ADDSP(new ImmediateInteger(nTotal)));
        compiler.getStackManager().useTemp(nTotal);

        object.codeGenExpr(compiler, Register.R1);
        compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(0, Register.SP)));

        for (int i = 0; i < nArgs; i++) {
            args.get(i).codeGenExpr(compiler, Register.R1);
            compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(-1 - i, Register.SP)));
        }

        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.getR(2)));
        compiler.addInstruction(new CMP(new NullOperand(), Register.getR(2)));
        compiler.addInstruction(new BEQ(nullDeref));

        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(2)), Register.getR(2)));

        MethodDefinition mdef = method.getMethodDefinition();
        int slot = 1 + mdef.getIndex();
        compiler.addInstruction(new BSR(new RegisterOffset(slot, Register.getR(2))));

        compiler.addInstruction(new SUBSP(new ImmediateInteger(nTotal)));
        compiler.getStackManager().releaseTemp(nTotal);

        if (mdef.getType().isVoid()) {
            return;
        }

        compiler.addInstruction(new LOAD(Register.R0, Register.R1));
        if (target.getNumber() != Register.R1.getNumber()) {
            compiler.addInstruction(new LOAD(Register.R1, target));
        }
    }

    @Override
    protected void codeGenByteExpr(DecacCompiler compiler) {
        throw new UnsupportedOperationException(
            "L'opérateur 'instanceof' n'est pas supporté dans l'extension BYTE (périmètre Sans Objet uniquement)."
        );
    }
}
