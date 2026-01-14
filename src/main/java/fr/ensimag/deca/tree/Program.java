package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.codegen.ErrorManager;
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
        
        classes.verifyListClass(compiler);

        classes.verifyListClassMembers(compiler);

        classes.verifyListClassBody(compiler);

        main.verifyMain(compiler);

        LOG.debug("End verifyProgram");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {

        compiler.addComment("Main program");

        compiler.getRegAllocator().reset();
        compiler.getStackManager().resetTemp();
        compiler.getStackManager().resetVars();

        main.codeGenMain(compiler);

        int d2 = compiler.getStackManager().getTSTOForMain();

        // Label pilePleine = compiler.getErrorManager().label(ErrorManager.RuntimeError.STACK_OVERFLOW);

        compiler.addFirst(new ADDSP(new ImmediateInteger(d2)));
        // BOV : seulement si -n n'est PAS actif
        if (!compiler.getNoCheckOption()) {
            compiler.addFirst(
                new BOV(
                    compiler.getErrorManager()
                            .label(ErrorManager.RuntimeError.STACK_OVERFLOW)
                )
            );
        }
        compiler.addFirst(new TSTO(new ImmediateInteger(d2)));
        compiler.addFirstComment("Main Program");

        compiler.addInstruction(new HALT());

        compiler.getErrorManager().emitHandlers(compiler);
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

    @Override
    public void codeGenByte(DecacCompiler compiler) {

        // En-tête du .class
        compiler.getByteManager().getInstructions().add(
            ".class public Main"
        );
        compiler.getByteManager().getInstructions().add(
            ".super java/lang/Object"
        );
        compiler.getByteManager().getInstructions().add("");

        // Méthode main
        compiler.getByteManager().getInstructions().add(
            ".method public static main([Ljava/lang/String;)V"
        );

        // Limites JVM (larges pour le projet GL)
        compiler.getByteManager().getInstructions().add(
            ".limit stack 50"
        );

        // D'abord il faut appeler main.codeGenMainByte
        // int locals = Math.max(1, compiler.getNextLocalSlot());
        // compiler.getByteManager().getInstructions().add(".limit locals " + locals);


        compiler.getByteManager().getInstructions().add(
            ".limit locals 50"
        );

        // Corps du main
        main.codeGenMainByte(compiler);

        // return + fin de méthode
        compiler.getByteManager().emitReturnVoid();
        compiler.getByteManager().getInstructions().add(
            ".end method"
        );
    }



}
