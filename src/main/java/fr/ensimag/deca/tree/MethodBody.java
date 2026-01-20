package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 * Represents the body of a method: variables + instructions.
 */
public class MethodBody extends Tree{
    private final ListDeclVar vars;
    private final ListInst insts;

    public MethodBody(ListDeclVar vars, ListInst insts) {
        super();
        this.vars  = vars;
        this.insts = insts;
    }

    public ListDeclVar getVars() {
        return vars;
    }

    public ListInst getInsts() {
        return insts;
    }
    public void iterChildren(TreeFunction f) {
        vars.iter(f);
        insts.iter(f);
    }
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        vars.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
    public void decompile(IndentPrintStream s) {
        s.print("{");
        vars.decompile(s);
        insts.decompile(s);
        s.print("}");
    }
}
