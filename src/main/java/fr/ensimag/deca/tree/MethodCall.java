package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;

import org.apache.commons.lang.Validate;

import java.io.PrintStream;

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
    public Type verifyExpr(DecacCompiler compiler,
                           EnvironmentExp localEnv,
                           ClassDefinition currentClass)
            throws ContextualError {

        Type objType = object.verifyExpr(compiler, localEnv, currentClass);

        if (!objType.isClass()) {
            throw new ContextualError(
                "Appel de méthode sur un type non-objet",
                getLocation()
            );
        }

        ClassDefinition classDef =
            ((ClassType) objType).getDefinition();

        Definition def =
            classDef.getMembers().get(method.getName());

        if (!(def instanceof MethodDefinition)) {
            throw new ContextualError(
                method.getName() + " n'est pas une méthode",
                method.getLocation()
            );
        }

        MethodDefinition methodDef = (MethodDefinition) def;

        arguments.verifyListExpr(
            compiler,
            localEnv,
            currentClass
        );

        method.setDefinition(methodDef);
        setType(methodDef.getType());

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
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {
             throw new UnsupportedOperationException("MethodCall codeGen not implemented yet");
}

}
