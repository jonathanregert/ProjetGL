package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
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

        compiler.addComment("Main program");

        compiler.getRegAllocator().reset();
        compiler.getStackManager().resetTemp();
        compiler.getStackManager().resetVars();

        // --- Réserver la vtable de Object (builtin) ---
        Symbol objectSymb = compiler.createSymbol("Object");
        TypeDefinition tdObj = compiler.environmentType.get(objectSymb);
        if (!(tdObj instanceof ClassDefinition)) {
            throw new IllegalStateException("Object builtin n'est pas une ClassDefinition");
        }
        ClassDefinition objectDef = (ClassDefinition) tdObj;

        int sizeObj = 1 + objectDef.getNumberOfMethods();
        RegisterOffset baseObj = compiler.getStackManager().allocGlobalBlock(sizeObj);
        objectDef.setAddrTable(baseObj);

        // --- Construction vtable Object : case 0 = null ---
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, objectDef.getAddrTable()));

        // (optionnel plus tard si Object a equals)
        // compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")), Register.R0));
        // compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(baseObj.getOffset() + 1, Register.GB)));

        // 1) Passe 1 : réserver la place des vtables des classes utilisateur
        classes.codeGenMTable(compiler);

        // 2) Construction des vtables des classes utilisateur
        classes.codeGenBuildMTable(compiler);

        // 3) Programme principal
        main.codeGenMain(compiler);

        // 4) Prologue global (poly)
        int addsp = compiler.getStackManager().getGlobalCount();          // vtables + variables globales
        int tsto  = addsp + compiler.getStackManager().getMaxTemp();      // + temporaires max

        compiler.addFirst(new ADDSP(new ImmediateInteger(addsp)));

        if (!compiler.getNoCheckOption()) {
            compiler.addFirst(new BOV(
                compiler.getErrorManager().label(ErrorManager.RuntimeError.STACK_OVERFLOW)
            ));
        }

        compiler.addFirst(new TSTO(new ImmediateInteger(tsto)));
        compiler.addFirstComment("Main Program");

        // 5) Fin partie principale
        compiler.addInstruction(new HALT());

        // 6) Handlers d'erreurs
        compiler.getErrorManager().emitHandlers(compiler);

        // 7) Sous-programmes (après HALT)
        classes.codeGenInit(compiler);
        classes.codeGenMethods(compiler);
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
