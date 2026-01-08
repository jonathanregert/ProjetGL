package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;


/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl42
 * @date 01/01/2026
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("Start verifyProgram");
        
        // Pass 1
        classes.verifyListClass(compiler);

        // Pass 2
        classes.verifyListClassMembers(compiler);

        // Pass 3
        classes.verifyListClassBody(compiler);

        // Main
        main.verifyMain(compiler);

        LOG.debug("End verifyProgram");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {

        compiler.addComment("Main program");

        // reset
        compiler.getRegAllocator().reset();
        compiler.getStackManager().resetTemp();
        compiler.getStackManager().resetVars();

        // 1) Générer le code du main (ça met à jour maxTemp et globalCount)
        main.codeGenMain(compiler);

        // 2) Calculer d1/d2        
        int d2 = compiler.getStackManager().getTSTOForMain();

        // 3) Patch prologue (ordre inverse car insertion en tête)
        compiler.addFirst(new ADDSP(new ImmediateInteger(d2)));
        compiler.addFirst(new BOV(new Label("pile_pleine")));
        compiler.addFirst(new TSTO(new ImmediateInteger(d2)));
        compiler.addFirstComment("Main Program");

        // 4) Fin normale
        compiler.addInstruction(new HALT());

        // 5) Messages d'erreurs
        compiler.addComment("Message d'erreurs");
        compiler.addLabel(new Label("pile_pleine"));
        compiler.addInstruction(new WSTR("Erreur : debordement de pile"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
