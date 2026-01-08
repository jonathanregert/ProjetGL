package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
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
        // verifier que la classe existe
        Type t = className.verifyType(compiler);
        this.setType(t);
        return t;
    }

    @Override
    public void codeGenExpr(DecacCompiler compiler, GPRegister register) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}