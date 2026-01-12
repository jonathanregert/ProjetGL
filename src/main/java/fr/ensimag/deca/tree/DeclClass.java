package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorManager.RuntimeError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.codegen.StackManager;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.NullOperand;

import static org.mockito.ArgumentMatchers.*;

import java.io.PrintStream;
// import java.lang.instrument.ClassDefinition;

import org.apache.commons.lang.Validate;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl42
 * @date 01/01/2026
 */
public class DeclClass extends AbstractDeclClass {

    private AbstractIdentifier ClassName;
    private AbstractIdentifier ClassExtention;
    private ListDeclField classFields;
    private ListDeclMethod classMethods;

    private ClassDefinition classDefinition;

    public DeclClass(AbstractIdentifier className, AbstractIdentifier classExtension, ListDeclField classFields, ListDeclMethod classMethods) {

        Validate.notNull(className);
        Validate.notNull(classExtension);
        Validate.notNull(classFields);
        Validate.notNull(classMethods);

        this.ClassName = className;
        this.ClassExtention = classExtension;
        this.classFields = classFields;
        this.classMethods = classMethods;
    
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class { ... A FAIRE ... }");
    }


    // (1.3)
    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {

        // Super classe existence : condition donnée
        if (!compiler.environmentType.isDeclared(ClassExtention.getName())) {
            throw new ContextualError(
                "Super-classe " + ClassExtention.getName() + " non déclarée", 
                getLocation()
            );
        }
        if (ClassName.getName().getName().equals("int") ||
            ClassName.getName().getName().equals("float") ||
            ClassName.getName().getName().equals("void") ||
            ClassName.getName().getName().equals("boolean") ||
            ClassName.getName().getName().equals("string")) {
            throw new ContextualError(
                    "Le nom de la classe " + ClassName.getName() + " est interdit", 
                    getLocation()
                );
            }

        // La classe elle meme n'existe pas déjà
        if (compiler.environmentType.isDeclared(ClassName.getName())) {
            // Si le nom de la classe est int, float, void, boolean ou string
            if (ClassName.getName().getName().equals("int") ||
                ClassName.getName().getName().equals("float") ||
                ClassName.getName().getName().equals("void") ||
                ClassName.getName().getName().equals("boolean") ||
                ClassName.getName().getName().equals("string")) {
                throw new ContextualError(
                    "Le nom de la classe " + ClassName.getName() + " est interdit", 
                    getLocation()
                );
            }
            throw new ContextualError(
                "Classe " + ClassName.getName() + " déjà déclarée", 
                getLocation()
            );
        }

    TypeDefinition def = compiler.environmentType.get(ClassExtention.getName());

    // TypeDefinition superClassDef = (ClassDefinition) compiler.environmentType.get(ClassExtention.getName());
    if (def == null || !def.getType().isClass()) {
        throw new ContextualError(
            "La super-classe " + ClassExtention.getName() + " n'est pas une classe", 
            getLocation()
        );
    }

    // on cast apres le check
    ClassDefinition superClassDef = (ClassDefinition) def;

    ClassExtention.setDefinition(superClassDef);

    ClassType newClassType = new ClassType(ClassName.getName(), ClassName.getLocation(), (fr.ensimag.deca.context.ClassDefinition) superClassDef);
    ClassDefinition superClassDefinition = (ClassDefinition) superClassDef;

    this.classDefinition = new ClassDefinition(newClassType, getLocation(), superClassDefinition);


        
    
    try {
        // On l'ajoute à l'environnement global des types
        compiler.environmentType.declare(ClassName.getName(), this.classDefinition);
        } catch (EnvironmentType.DoubleDefException e) {
        throw new ContextualError("Double définition de la classe " + ClassName.getName(), ClassName.getLocation());
        }

        ClassName.setDefinition(this.classDefinition); // deco        

        }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        this.classFields.verifyListDeclField(compiler, this.classDefinition);
        this.classMethods.verifyListDeclMethode(compiler, this.classDefinition);
    }

    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        this.classFields.verifyListDeclFieldInitialization(compiler, this.classDefinition);
        this.classMethods.verifyListDeclMethodBody(compiler, this.classDefinition);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        s.println(prefix + "name:");
        ClassName.prettyPrint(s, prefix + "  ", false);

        s.println(prefix + "extends:");
        ClassExtention.prettyPrint(s, prefix + "  ", true);
        classFields.prettyPrint(s, prefix, true);
        classMethods.prettyPrint(s, prefix, true);
    }


    @Override
    public AbstractIdentifier getName() {
        return ClassName;
    }


    @Override
    protected void iterChildren(TreeFunction f) {
        ClassName.iter(f);
        ClassExtention.iter(f);
        classMethods.iter(f);
    }

    // Gencode

    @Override
    public void codeGenMTable(DecacCompiler compiler){
        ClassDefinition classDef = this.classDefinition;

        int tailleTable = 1 + classDef.getNumberOfMethods();

        RegisterOffset base = compiler.getStackManager().allocGlobalBlock(tailleTable);

        // On stocke l'adresse dans la ClassDefinition
        classDef.setAddrTable(base);
        
        compiler.addComment("Passe1 table de méthode : " + classDef.getType().getName()
            + " base=" + base + " taille=" + tailleTable);
    }

    @Override
    public void codeGenBuildMTable(DecacCompiler compiler) {
        ClassDefinition classDef = this.classDefinition;
        RegisterOffset base = classDef.getAddrTable(); // base de la table en GB

        // case 0
        if (classDef.getSuperClass() == null){
            // classe racine = Object
            compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
            // compiler.addInstruction(new LOAD(new ImmediateNull(), Register.R0));
            compiler.addInstruction(new STORE(Register.R0, base));
        } else {
            RegisterOffset superBase = classDef.getSuperClass().getAddrTable();
            compiler.addInstruction(new LEA(superBase, Register.R0));
            compiler.addInstruction(new STORE(Register.R0, base));
        }

        // 1) Héritage : recopier les pointeurs de méthodes depuis la super-classe
        // si super existe
        if (classDef.getSuperClass() != null){
            int n = classDef.getNumberOfMethods();
            RegisterOffset superBase = classDef.getSuperClass().getAddrTable();

            for (int i = 0; i < n; i++){
                RegisterOffset src = new RegisterOffset(superBase.getOffset() + 1 + i, Register.GB);
                RegisterOffset dst = new RegisterOffset(base.getOffset() + 1 + i, Register.GB);

                compiler.addInstruction(new LOAD(src, Register.R0));
                compiler.addInstruction(new STORE(Register.R0, dst));
            }
        }

        // 2) Redéfinition / ajout : on écrit les mthd déclarées dans la classe courante
        for (AbstractDeclMethod m : classMethods.getList()){
        MethodDefinition md = m.getMethodName().getMethodDefinition();
        int index = md.getIndex();
        Label label = md.getLabel();

        RegisterOffset cell = new RegisterOffset(base.getOffset() + 1 + index, Register.GB);
        compiler.addInstruction(new LOAD(new LabelOperand(label), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, cell));
    }
    }

    @Override
    public void codeGenInit(DecacCompiler compiler) {
        ClassDefinition classDef = this.classDefinition;
        String className = classDef.getType().getName().getName();

        Label initLabel = new Label("init." + className);
        Label pilePleine = compiler.getErrorManager().label(RuntimeError.STACK_OVERFLOW);

        compiler.addLabel(initLabel);
        compiler.addComment("init." + className + " (this dans R1)");

        compiler.getRegAllocator().reset();
        compiler.getStackManager().enterBlock();

        // On bufferise le bloc pour pouvoir insérer TSTO/BOV au début après coup
        compiler.beginBlock();

        int first = 3;
        int last = compiler.getRegAllocator().getMaxReg();

        // Sauvegarde pessimiste des registres temporaires
        for (int r = first; r <= last; r++) {
            compiler.addToBlock(new PUSH(Register.getR(r)));
            compiler.getStackManager().useTemp(1);
        }

        // TODO : init des champs (à ajouter après)
        // classFields.codeGenInitFields(compiler, classDef);

        for (int r = last; r >= first; r--) {
            compiler.addToBlock(new POP(Register.getR(r)));
            compiler.getStackManager().releaseTemp(1);
        }

        compiler.addToBlock(new RTS());

        // Maintenant on connaît d (poly)
        int d = compiler.getStackManager().getTSTOForLocals();
        compiler.addFirstToBlock(new TSTO(new ImmediateInteger(d)));
        compiler.addFirstToBlock(new BOV(pilePleine));

        compiler.endBlock();
        compiler.getStackManager().exitBlock();
    }

    @Override
    public void codeGenMethods(DecacCompiler compiler) {
        String className = this.classDefinition.getType().getName().getName();

        compiler.addComment("Code des méthodes de la classe " + className);

        compiler.setCurrentClassName(className);

        for (AbstractDeclMethod m : classMethods.getList()){
            m.codeGenDeclMethod(compiler);
        }

        compiler.setCurrentClassName(null);
    }

}
