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
import fr.ensimag.ima.pseudocode.instructions.BSR;
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

        String className = ClassName.getName().getName();
        if (ClassName.getName().getName().equals("int") ||
                ClassName.getName().getName().equals("float") ||
                ClassName.getName().getName().equals("void") ||
                ClassName.getName().getName().equals("boolean") ||
                ClassName.getName().getName().equals("string")) {
                throw new ContextualError(
                "Le nom '" + className + "' est un type prédéfini et ne peut pas être utilisé comme nom de classe.", getLocation());

            }

        try {
            compiler.environmentType.declare(ClassName.getName(), this.classDefinition);
        } catch (EnvironmentType.DoubleDefException e) {
            // non existe deja
            throw new ContextualError("La classe " + className + " est déjà déclarée dans ce programme.", ClassName.getLocation());
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

    ClassType newClassType = new ClassType(ClassName.getName(), ClassName.getLocation(), superClassDef);
    ClassDefinition superClassDefinition = superClassDef;

    this.classDefinition = new ClassDefinition(newClassType, getLocation(), superClassDefinition);


    newClassType.setDefinition(this.classDefinition); // pour selection fields


    ClassName.setDefinition(this.classDefinition); // deco
    
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        this.classFields.verifyListDeclField(compiler, this.classDefinition);
        // if (this.classDefinition.getSuperClass() != null) {
        //     this.classDefinition.setNumberOfMethods(
        //         this.classDefinition.getSuperClass().getNumberOfMethods()
        //     );
        // } else {
        //     this.classDefinition.setNumberOfMethods(0);
        // }
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
        classFields.iter(f);
        classMethods.iter(f);
    }

    // Gencode
    private boolean hasAnyFieldsInHierarchy(ClassDefinition cd) {
        while (cd != null) {
            if (cd.getNumberOfFields() > 0) return true; // champs déclarés dans cette classe
            cd = cd.getSuperClass();
        }
        return false;
    }

    @Override
    public void codeGenMTable(DecacCompiler compiler){
        ClassDefinition classDef = this.classDefinition;

        int tailleTable = 1 + classDef.getNumberOfMethods();

        RegisterOffset base = compiler.getStackManager().allocGlobalBlock(tailleTable);
        classDef.setAddrTable(base);
        
        compiler.addComment("Passe1 table de méthode : " + classDef.getType().getName()
            + " base=" + base + " taille=" + tailleTable);
    }

    @Override
    public void codeGenBuildMTable(DecacCompiler compiler) {
        ClassDefinition classDef = this.classDefinition;
        RegisterOffset base = classDef.getAddrTable();
        if (base == null) {
            throw new IllegalStateException("VTable non réservée pour " + classDef.getType());
        }

        // Case 0 : pointeur vers vtable de la super-classe (ou null pour Object)
        if (classDef.getSuperClass() == null) {
            compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
            compiler.addInstruction(new STORE(Register.R0, base));
        } else {
            RegisterOffset superBase = classDef.getSuperClass().getAddrTable();
            if (superBase == null) {
                throw new IllegalStateException(
                        "La table de la super-classe est non initialisée: " + classDef.getSuperClass().getType());
            }
            compiler.addInstruction(new LEA(superBase, Register.R0));
            compiler.addInstruction(new STORE(Register.R0, base));

            // Héritage : copier UNIQUEMENT les méthodes de la super-classe
            int nSuper = classDef.getSuperClass().getNumberOfMethods();
            for (int i = 0; i < nSuper; i++) {
                RegisterOffset src = new RegisterOffset(superBase.getOffset() + 1 + i, Register.GB);
                RegisterOffset dst = new RegisterOffset(base.getOffset() + 1 + i, Register.GB);
                compiler.addInstruction(new LOAD(src, Register.R0));
                compiler.addInstruction(new STORE(Register.R0, dst));
            }
        }

        // Écraser/ajouter les méthodes déclarées dans la classe courante
        for (AbstractDeclMethod m : classMethods.getList()) {
            MethodDefinition md = m.getMethodName().getMethodDefinition();
            int index = md.getIndex(); // commence à 0
            Label label = md.getLabel();

            RegisterOffset cell = new RegisterOffset(base.getOffset() + 1 + index, Register.GB);
            // On stocke l’adresse du code dans la vtable (via LabelOperand)
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
        compiler.addComment("init." + className + " (this = -2(LB))");

        compiler.getRegAllocator().reset();
        compiler.getStackManager().enterBlock();

        // Champs locaux (déclarés dans la classe courante)
        boolean hasLocalFields = !classFields.getList().isEmpty();

        // Option A : appeler init.super seulement si la super-classe (ou ses ancêtres) a des champs
        ClassDefinition superDef = classDef.getSuperClass();
        boolean callSuperInit = false;
        if (superDef != null) {
            String sname = superDef.getType().getName().getName();
            if (!"Object".equals(sname) && hasAnyFieldsInHierarchy(superDef)) {
                callSuperInit = true;
            }
        }

        // Si rien à faire : RTS direct, mais on doit sortir du block StackManager !
        if (!hasLocalFields && !callSuperInit) {
            compiler.addInstruction(new RTS());
            compiler.getStackManager().exitBlock();
            return;
        }

        compiler.beginBlock();

        int first = compiler.getRegAllocator().getFirstAlloc();
        int last = compiler.getRegAllocator().getMaxReg();

        for (int r = first; r <= last; r++) {
            compiler.addToBlock(new PUSH(Register.getR(r)));
            compiler.getStackManager().useTemp(1);
        }

        if (callSuperInit) {
            String sname = superDef.getType().getName().getName();

            compiler.addToBlock(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
            compiler.addToBlock(new PUSH(Register.R0));
            compiler.getStackManager().useTemp(1);

            compiler.addToBlock(new BSR(new Label("init." + sname)));

            compiler.addToBlock(new POP(Register.R0));
            compiler.getStackManager().releaseTemp(1);
        }

        classFields.codeGenInitFields(compiler, classDef);

        for (int r = last; r >= first; r--) {
            compiler.addToBlock(new POP(Register.getR(r)));
            compiler.getStackManager().releaseTemp(1);
        }

        compiler.addToBlock(new RTS());

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
