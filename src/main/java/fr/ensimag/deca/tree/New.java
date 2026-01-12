package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;


public class New extends AbstractExpr {
    private AbstractIdentifier className;

    public New(AbstractIdentifier className) {
        Validate.notNull(className);
        this.className = className;
    }
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new " + className.getName() + "()");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        className.prettyPrint(s, prefix + "  ", false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        className.iter(f);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type t = className.verifyType(compiler);
        if (!t.isClass()) {
        throw new ContextualError("L'opérateur 'new' ne s'applique qu'aux classes.", 
                                   className.getLocation());
        }
        this.setType(t);
        return t;
    }

    @Override
    public void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        ClassDefinition classDef = className.getClassDefinition();

        int taille = 1 + classDef.getNumberOfFields();

        compiler.addInstruction(new NEW(new ImmediateInteger(taille), target));

        RegisterOffset mTableBase = classDef.getAddrTable();

        compiler.addInstruction(new LOAD(mTableBase, Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0, target)));

        if (target.getNumber() != Register.R1.getNumber()){
            compiler.addInstruction(new LOAD(target, Register.R1));
        }

        String className = classDef.getType().getName().getName();
        Label initLabel = new Label("init." + className);
        compiler.addInstruction(new BSR(initLabel));

        if (target.getNumber() != Register.R1.getNumber()){
            compiler.addInstruction(new LOAD(Register.R1, target));
        }
    }

}