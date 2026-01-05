package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import org.apache.commons.lang.Validate;

/**
 * Empty main Deca program
 *
 * @author gl42
 * @date 01/01/2026
 */
public class EmptyMain extends AbstractMain {
    private final ListInst listInst;

    public EmptyMain(ListInst listInst) {
        Validate.notNull(listInst);
        this.listInst = listInst;
    }

    public EmptyMain() {
    this.listInst = new ListInst(); // une liste vide
}


    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
       getListInst().verifyListInst(compiler, null, null, null);
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public ListInst getListInst() {
        return listInst;
    }
    /**
     * Contains no real information => nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("{\n");
        s.indent();
        listInst.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
         listInst.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
       listInst.prettyPrint(s, prefix, true);
    }
}
