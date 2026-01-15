package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;


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

        compiler.getRegAllocator().reset();
        compiler.getStackManager().resetTemp();
        compiler.getStackManager().resetVars();

        // On démarre un bloc global : permet d'insérer le prologue (TSTO/BOV/ADDSP) AVANT tout
        compiler.beginBlock();

        // -----------------------------
        // A) Prologue global (poly)
        // -----------------------------
        int addsp = compiler.getStackManager().getGlobalCount();
        int tsto  = addsp + compiler.getStackManager().getMaxTemp();

        compiler.addFirstToBlock(new TSTO(new ImmediateInteger(tsto)), "Main Program");
        if (!compiler.getNoCheckOption()) {
            compiler.addFirstToBlock(new BOV(
                compiler.getErrorManager().label(ErrorManager.RuntimeError.STACK_OVERFLOW)
            ));
        }
        compiler.addFirstToBlock(new ADDSP(new ImmediateInteger(addsp)));

        // -----------------------------
        // B) Construction des tables de méthodes
        // -----------------------------
        compiler.addCommentToBlock("--------------------------------------------------");
        compiler.addCommentToBlock("Construction des tables des methodes");
        compiler.addCommentToBlock("--------------------------------------------------");

        // Builtin: Object (vtable + equals)
        ClassDefinition objectDef = compiler.environmentType.OBJECT.getDefinition();

        int sizeObj = 1 + objectDef.getNumberOfMethods();
        RegisterOffset baseObj = compiler.getStackManager().allocGlobalBlock(sizeObj);
        objectDef.setAddrTable(baseObj);

        // parent = null
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, baseObj));

        // equals
        MethodDefinition eq;
        try {
            eq = objectDef.getMembers()
                .get(compiler.createSymbol("equals"))
                .asMethodDefinition("Object.equals doit être une méthode", Location.BUILTIN);
        } catch (ContextualError e) {
            throw new IllegalStateException("Builtin Object.equals mal défini", e);
        }

        compiler.addInstruction(new LOAD(new LabelOperand(eq.getLabel()), Register.R0));
        compiler.addInstruction(new STORE(
            Register.R0,
            new RegisterOffset(baseObj.getOffset() + 1 + eq.getIndex(), baseObj.getRegister())
        ));

        // Classes utilisateur : réserver + remplir vtables
        classes.codeGenMTable(compiler);
        classes.codeGenBuildMTable(compiler);

        // -----------------------------
        // C) Programme principal
        // -----------------------------
        compiler.addCommentToBlock("--------------------------------------------------");
        compiler.addCommentToBlock("Code du programme principal");
        compiler.addCommentToBlock("--------------------------------------------------");

        main.codeGenMain(compiler);

        // Fin main
        compiler.addInstruction(new HALT());

        // Fin du bloc global (flush prefix + body dans le programme)
        compiler.endBlock();

        // -----------------------------
        // D) Builtins / sous-programmes (après HALT)
        // -----------------------------
        emitObjectEquals(compiler);
        classes.codeGenInit(compiler);
        classes.codeGenMethods(compiler);

        // Handlers d'erreurs (après tout)
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

    private void emitObjectEquals(DecacCompiler compiler) {
        Label lTrue = new Label("Object.equals.true");
        Label lEnd  = new Label("Object.equals.end");

        compiler.addLabel(new Label("code.Object.equals"));

        // R0 = this
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
        // compare to other
        compiler.addInstruction(new CMP(new RegisterOffset(-3, Register.LB), Register.R0));
        // if equal -> true
        compiler.addInstruction(new BEQ(lTrue));

        // false
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.R0));
        compiler.addInstruction(new BRA(lEnd));

        // true
        compiler.addLabel(lTrue);
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), Register.R0));

        compiler.addLabel(lEnd);
        compiler.addInstruction(new RTS());
    }
}
