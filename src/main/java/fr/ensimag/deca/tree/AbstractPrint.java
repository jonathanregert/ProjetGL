package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Print statement (print, println, ...).
 *
 * @author gl42
 * @date 01/01/2026
 */
public abstract class AbstractPrint extends AbstractInst {

    private boolean printHex;
    private ListExpr arguments = new ListExpr();
    
    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        for (AbstractExpr a : getArguments().getList()) {
            Type t = a.verifyExpr(compiler, localEnv, currentClass);
            if (!(t.isInt() || t.isFloat() || t.isString() || t.isBoolean())) {
                throw new ContextualError("Type " + t + " n'est pas imprimable",
                    a.getLocation());
            }
        }
    }

    

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        for (AbstractExpr a : getArguments().getList()) {
            a.codeGenPrint(compiler);
        }
    }

    private boolean getPrintHex() {
        return printHex;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("print");
        s.print(getSuffix());
        s.print("(");
        arguments.decompile(s);
        s.print(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

    @Override
    protected void codeGenByte(DecacCompiler compiler) {
        for (AbstractExpr e : getArguments().getList()) {

            if (e.getType().isBoolean()) {
                // System.out.println("Print BOOLEAN");
                String t = compiler.getByteManager().newLabel();
                String f = compiler.getByteManager().newLabel();
                String end = compiler.getByteManager().newLabel();

                e.codeGenByteCond(compiler, t, f);

                compiler.getByteManager().emitLabel(t);
                compiler.getByteManager().emitLDC(1);
                compiler.getByteManager().emitGoto(end);

                compiler.getByteManager().emitLabel(f);
                compiler.getByteManager().emitLDC(0);

                compiler.getByteManager().emitLabel(end);
            } else {
                // System.out.println("Print NON-BOOLEAN");
                e.codeGenByteExpr(compiler);
            }

            if ("ln".equals(getSuffix())) {
                compiler.getByteManager().emitPrintln(e.getType());
            } else {
                compiler.getByteManager().emitPrint(e.getType());
            }

        }
    }

}
