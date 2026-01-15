package fr.ensimag.deca;

import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.codegen.RegAllocator;
import fr.ensimag.deca.codegen.StackManager;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.ima.pseudocode.AbstractLine;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.InlinePortion;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Logger;

/**
 * Decac compiler instance.
 *
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 *
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl42
 * @date 01/01/2026
 */
public class DecacCompiler {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);
    
    /**
     * Portable newline character.
     */
    private static final String nl = System.getProperty("line.separator", "\n");
    private int labelId = 0; // pour generation de code et pouvoir jump au bon label : label_labelId
    private final RegAllocator regAllocator;
    private final StackManager stackManager = new StackManager();
    private final ErrorManager errorManager = new ErrorManager();
    private ArrayList<Line> blockPrefix = null;
    private ArrayList<Line> blockBody = null;
    private Label currentMethodEndLabel;
    private String currentClassName;

    public void setCurrentClassName(String n) { currentClassName = n; }
    public String getCurrentClassName() { return currentClassName; }


    public void setCurrentMethodEndLabel(Label l) { currentMethodEndLabel = l; }
    public Label getCurrentMethodEndLabel() { return currentMethodEndLabel; }

    public void addInline(String asm) {
        program.add(new InlinePortion(asm));
    }
    
    public void beginBlock() {
        if (blockBody != null || blockPrefix != null) {
            throw new IllegalStateException("Bloc déjà ouvert");
        }
        blockPrefix = new ArrayList<>();
        blockBody = new ArrayList<>();
    }

    public void addToBlock(Line l) {
        if (blockBody == null) program.add(l);
        else blockBody.add(l);
    }


    // Prefix: sans commentaire
    public void addFirstToBlock(Instruction ins) {
        addLineToPrefix(new Line(ins));
    }

    // Prefix: avec commentaire (tu l'as déjà, je la remets pour cohérence)
    public void addFirstToBlock(Instruction ins, String comment) {
        addLineToPrefix(new Line(null, ins, comment));
    }

    // Optionnel: prefix commentaire seul
    public void addFirstCommentToBlock(String comment) {
        addLineToPrefix(new Line(comment));
    }


    public void endBlock() {
        if (blockBody == null || blockPrefix == null) {
            throw new IllegalStateException("Aucun bloc ouvert");
        }

        // 1) prefix d'abord (TSTO/BOV/...)
        for (Line l : blockPrefix) {
            program.add(l);
        }

        // 2) corps ensuite (labels + instructions + comments dans l'ordre)
        for (Line l : blockBody) {
            program.add(l);
        }

        blockPrefix = null;
        blockBody = null;
    }


    public void addCommentToBlock(String comment) {
        addLineToBody(new Line(comment));
    }



    public void addLabelToBlock(Label lab) {
        addLineToBody(new Line(lab));
    }

    //Helpers internes
    private void addLineToBody(Line l) {
        if (blockBody != null) {
            blockBody.add(l);
        } else {
            program.add(l);
        }
    }

    private void addLineToPrefix(Line l) {
        if (blockPrefix != null) {
            blockPrefix.add(l);
        } else {
            // Pas de bloc ouvert => on veut vraiment "au tout début" du programme
            program.addFirst(l); // IMAProgram.addFirst(Line)
        }
    }

    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();
        this.compilerOptions = compilerOptions;
        this.source = source;

        int X = compilerOptions.getRegisterCount();
        this.regAllocator = new RegAllocator(X-1);
    }

    public RegAllocator getRegAllocator() {
        return regAllocator;
    }

    public StackManager getStackManager(){
        return stackManager;
    }

    public boolean getNoCheckOption() {
        return compilerOptions.getNoCheckOption();
    }

    /**
     * Gestion des errurs
     */
    public ErrorManager getErrorManager(){
        return errorManager;
    }

    public void addFirst(Instruction i, String comment) {
        program.addFirst(i, comment);
    }

    public void addFirstComment(String s) {
        program.addFirst(new Line(s));
    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }


    public int getLabelId()
    {
        return labelId++;
    }


    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        program.add(line);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        program.addComment(comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        program.addLabel(label);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction ins) {
        addLineToBody(new Line(ins));
    }

    public void addInstruction(Instruction ins, String comment) {
        addLineToBody(new Line(null, ins, comment));
    }

    /**
     * @see 
     * fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return program.display();
    }
    
    private final CompilerOptions compilerOptions;
    private final File source;
    /**
     * The main program. Every instruction generated will eventually end up here.
     */
    private final IMAProgram program = new IMAProgram();


    /** The global environment for types (and the symbolTable) */
    public final SymbolTable symbolTable = new SymbolTable();
    @SuppressWarnings("this-escape")
    public final EnvironmentType environmentType = new EnvironmentType(this);

    public Symbol createSymbol(String name) {
        return symbolTable.create(name);
    }

    // Avoir les EnvType :
    public EnvironmentType getEnvTypes() {
        return environmentType;
    }
    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        String sourceFile = source.getAbsolutePath();
        String destFile = sourceFile.replaceAll("\\.deca$", ".ass");

        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }
    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName name of the destination (assembly) file
     * @param out stream to use for standard output (output of decac -p)
     * @param err stream to use to display compilation errors
     *
     * @return true on error
     */
    private boolean doCompile(String sourceName, String destName,
            PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);

        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }
        assert(prog.checkAllLocations());

        // option -p
        if (compilerOptions.getParseOption()) {
            prog.decompile(out);
            return false;
        }
        //

        prog.verifyProgram(this);
        assert(prog.checkAllDecorations());

        addComment("start main program");
        prog.codeGenProgram(this);
        addComment("end main program");
        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);

        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }

        LOG.info("Writing assembler file ...");

        program.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");
        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     *
     * @param sourceName Name of the file to parse
     * @param err Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError When an error prevented opening the source file
     * @throws DecacInternalError When an inconsistency was detected in the
     * compiler.
     * @throws LocationException When a compilation error (incorrect program)
     * occurs.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err)
            throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
            lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);
    }

}
