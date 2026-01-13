package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;

/**
 * Represents the body of a method asm = text
 */
public class MethodAsmBody extends Tree{
    private final StringLiteral asmText;

    public MethodAsmBody(StringLiteral asmText) {
        super();
        this.asmText = asmText;
    }

    public StringLiteral getAsmText() {
        return asmText;
    }
    public void iterChildren(TreeFunction f) {
        asmText.iter(f);
    }
    
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        asmText.prettyPrint(s, prefix, false);
    }

    public void decompile(IndentPrintStream s) {
        asmText.decompile(s);
    }

}
