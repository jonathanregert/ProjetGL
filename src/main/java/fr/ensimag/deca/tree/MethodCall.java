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
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

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

        int i = 0;
        for (AbstractExpr arg : arguments.getList()) {
        Type targetType = sig.paramNumber(i);
        // on verifie chaque argument
        AbstractExpr newArg = arg.verifyRValue(compiler, localEnv, currentClass, targetType);
        i++; // au lieu de faire set : immuable
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
        int nTotal = 1 + nArgs; // this + args explicites

        Label nullDeref = compiler.getErrorManager().label(RuntimeError.NULL_DEREFERENCE);

        // 1) Réserver la place des paramètres (poly)
        compiler.addInstruction(new ADDSP(new ImmediateInteger(nTotal)));
        compiler.getStackManager().useTemp(nTotal);

        // 2) Évaluer le param implicite (objet) et stocker en 0(SP)
        object.codeGenExpr(compiler, Register.getR(2)); // R2 = objet
        compiler.addInstruction(new STORE(Register.getR(2), new RegisterOffset(0, Register.SP)));

        // 3) Évaluer les args explicites de gauche à droite et stocker en -1(SP), -2(SP), ...
        for (int i = 0; i < nArgs; i++) {
            args.get(i).codeGenExpr(compiler, Register.getR(2)); // R2 = arg_i
            compiler.addInstruction(new STORE(Register.getR(2),
                    new RegisterOffset(-1 - i, Register.SP)));
        }

        // 4) Récupérer this depuis 0(SP) et vérifier null
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.getR(2)));
        compiler.addInstruction(new CMP(new NullOperand(), Register.getR(2)));
        compiler.addInstruction(new BEQ(nullDeref));

        // 5) Charger la vtable : R2 = *(this) = vtable
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(2)), Register.getR(2)));

        // 6) Appel virtuel : BSR (1 + index)(vtable)
        MethodDefinition mdef = method.getMethodDefinition();
        int slot = 1 + mdef.getIndex();
        compiler.addInstruction(new BSR(new RegisterOffset(slot, Register.getR(2))));

        // 7) Dépiler les paramètres (poly)
        compiler.addInstruction(new SUBSP(new ImmediateInteger(nTotal)));
        compiler.getStackManager().releaseTemp(nTotal);

        // 8) Résultat : R0 -> target
        if (mdef.getType().isVoid()) {
        // appel void : aucun résultat, on ne touche pas target
        return;
        }

        // résultat non-void : il est en R0 après BSR
        // ta convention veut le résultat final en R1
        compiler.addInstruction(new LOAD(Register.R0, Register.R1));

        // si le caller demande autre chose que R1, on copie
        if (target.getNumber() != Register.R1.getNumber()) {
            compiler.addInstruction(new LOAD(Register.R1, target));
        }
    }
}
