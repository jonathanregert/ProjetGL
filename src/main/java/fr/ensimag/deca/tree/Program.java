package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Line;
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

        compiler.beginBlock();

        Line lTsto = compiler.addFirstToBlockAndReturnLine(
            new TSTO(new ImmediateInteger(0)),
            "Main Program"
        );

        Line lBov = null;
        if (!compiler.getNoCheckOption()) {
            lBov = compiler.addFirstToBlockAndReturnLine(
                new BOV(compiler.getErrorManager().label(ErrorManager.RuntimeError.STACK_OVERFLOW))
            );
        }

        Line lAddsp = compiler.addFirstToBlockAndReturnLine(
            new ADDSP(new ImmediateInteger(0))
        );

        compiler.addCommentToBlock("--------------------------------------------------");
        compiler.addCommentToBlock("Construction des tables des methodes");
        compiler.addCommentToBlock("--------------------------------------------------");

        ClassDefinition objectDef = compiler.environmentType.OBJECT.getDefinition();

        int sizeObj = 1 + objectDef.getNumberOfMethods();
        RegisterOffset baseObj = compiler.getStackManager().allocGlobalBlock(sizeObj);
        objectDef.setAddrTable(baseObj);

        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, baseObj));

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

        classes.codeGenMTable(compiler);
        classes.codeGenBuildMTable(compiler);

        compiler.addCommentToBlock("--------------------------------------------------");
        compiler.addCommentToBlock("Code du programme principal");
        compiler.addCommentToBlock("--------------------------------------------------");

        main.codeGenMain(compiler);

        int addsp = compiler.getStackManager().getADDSPForMain();
        int tsto  = compiler.getStackManager().getTSTOForMain();

        lAddsp.setInstruction(new ADDSP(new ImmediateInteger(addsp)));
        lTsto.setInstruction(new TSTO(new ImmediateInteger(tsto)));

        compiler.addInstruction(new HALT());

        compiler.endBlock();

        emitObjectEquals(compiler);
        classes.codeGenInit(compiler);
        classes.codeGenMethods(compiler);

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

        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
        compiler.addInstruction(new CMP(new RegisterOffset(-3, Register.LB), Register.R0));
        compiler.addInstruction(new BEQ(lTrue));

        compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.R0));
        compiler.addInstruction(new BRA(lEnd));

        compiler.addLabel(lTrue);
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), Register.R0));

        compiler.addLabel(lEnd);
        compiler.addInstruction(new RTS());
    }

    @Override
    public void codeGenByte(DecacCompiler compiler) {

        // Main class
        compiler.getByteManager().startClass("Main", "java/lang/Object");

        // Méthode main
        compiler.getByteManager().getInstructions().add(
            ".method public static main([Ljava/lang/String;)V"
        );

        // on réserve temporairement la place dans la liste des intructions
        compiler.getByteManager().getInstructions().add(
            ".limit stack 0"
        );
        compiler.getByteManager().getInstructions().add(
            ".limit locals 0"
        );

        compiler.getByteManager().enterMethod();
        compiler.resetLocalSlots(1); // 0: args

        // Corps du main
        main.codeGenMainByte(compiler);

        // return + fin de méthode
        compiler.getByteManager().emitReturnVoid();

        // On corrige après passage de tout l'arbre
        int stack = compiler.getByteManager().getMaxStack();
        int locals = compiler.getMaxLocalSlot();

        compiler.getByteManager().getInstructions().set(4, ".limit stack " + stack);
        compiler.getByteManager().getInstructions().set(5, ".limit locals " + locals);

        compiler.getByteManager().getInstructions().add(
            ".end method"
        );

        // User classes (one Jasmin class per DeclClass)
        for (AbstractDeclClass c : classes.getList()) {
            if (c instanceof DeclClass) {
                ((DeclClass) c).codeGenByteClass(compiler);
            }
        }
    }



}
