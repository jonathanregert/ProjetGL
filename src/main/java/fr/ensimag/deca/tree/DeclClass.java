package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorManager.RuntimeError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
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
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.deca.codegen.ByteManager;

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
        s.print("class ");
        ClassName.decompile(s);
        s.print(" extends ");
        ClassExtention.decompile(s);
        s.println(" {");
        s.indent();
        classFields.decompile(s);
        classMethods.decompile(s);
        s.unindent();
        s.println("}");
    }


    // (1.3)
    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {

        // 1) Super-classe doit exister
        if (!compiler.environmentType.isDeclared(ClassExtention.getName())) {
            throw new ContextualError(
                "Super-classe " + ClassExtention.getName() + " non déclarée",
                getLocation()
            );
        }

        // 2) Nom interdit
        String cname = ClassName.getName().getName();
        if (cname.equals("int") || cname.equals("float") || cname.equals("void")
                || cname.equals("boolean") || cname.equals("string")) {
            throw new ContextualError(
                "Le nom '" + cname + "' est un type prédéfini et ne peut pas être utilisé comme nom de classe.",
                getLocation()
            );
        }


        // 4) Récupérer la super-classe et vérifier que c'est une classe
        TypeDefinition def = compiler.environmentType.get(ClassExtention.getName());
        if (def == null || !def.getType().isClass()) {
            throw new ContextualError(
                "La super-classe " + ClassExtention.getName() + " n'est pas une classe",
                getLocation()
            );
        }
        ClassDefinition superClassDef = (ClassDefinition) def;

        // 5) Construire la nouvelle classe
        ClassType newClassType = new ClassType(ClassName.getName(), ClassName.getLocation(), superClassDef);
        this.classDefinition = new ClassDefinition(newClassType, getLocation(), superClassDef);
        newClassType.setDefinition(this.classDefinition);

        // 6) Déclarer dans l'environnement des types
        try {
            compiler.environmentType.declare(ClassName.getName(), this.classDefinition);
        } catch (EnvironmentType.DoubleDefException e) {
            throw new ContextualError(
                "Double définition de la classe " + ClassName.getName(),
                ClassName.getLocation()
            );
        }

        // 7) Décorations
        ClassExtention.setDefinition(superClassDef);
        ClassName.setDefinition(this.classDefinition);
    }


    @Override
    protected void verifyClassMembers(DecacCompiler compiler) throws ContextualError {

        if (this.classDefinition.getSuperClass() != null) {
            this.classDefinition.setNumberOfFields(
                this.classDefinition.getSuperClass().getNumberOfFields()
            );
        } else {
            this.classDefinition.setNumberOfFields(0);
        }

        this.classFields.verifyListDeclField(compiler, this.classDefinition);

        if (this.classDefinition.getSuperClass() != null) {
            this.classDefinition.setNumberOfMethods(
                this.classDefinition.getSuperClass().getNumberOfMethods()
            );
        } else {
            this.classDefinition.setNumberOfMethods(0);
        }

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

            int nSuper = classDef.getSuperClass().getNumberOfMethods();
            for (int i = 0; i < nSuper; i++) {
                RegisterOffset src = new RegisterOffset(superBase.getOffset() + 1 + i, superBase.getRegister());
                RegisterOffset dst = new RegisterOffset(base.getOffset() + 1 + i, base.getRegister());
                compiler.addInstruction(new LOAD(src, Register.R0));
                compiler.addInstruction(new STORE(Register.R0, dst));
            }
        }

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
    private boolean hasAnyFieldsInHierarchy(ClassDefinition cd) {
        while (cd != null) {
            String name = cd.getType().getName().getName();
            if ("Object".equals(name)) return false;
            if (cd.getNumberOfFields() > 0) return true;
            cd = cd.getSuperClass();
        }
        return false;
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

        boolean hasLocalFields = !classFields.getList().isEmpty();

        ClassDefinition superDef = classDef.getSuperClass();
        boolean callSuperInit = false;
        if (superDef != null) {
            String sname = superDef.getType().getName().getName();
            if (!"Object".equals(sname) && hasAnyFieldsInHierarchy(superDef)) {
                callSuperInit = true;
            }
        }

        if (!hasLocalFields && !callSuperInit) {
            compiler.addInstruction(new RTS());
            compiler.getStackManager().exitBlock();
            return;
        }

        Line tstoLine = new Line(new TSTO(new ImmediateInteger(0)));
        compiler.add(tstoLine);
        compiler.addInstruction(new BOV(pilePleine));

        int first = compiler.getRegAllocator().getFirstAlloc();
        int last  = compiler.getRegAllocator().getMaxReg();

        for (int r = first; r <= last; r++) {
            compiler.addInstruction(new PUSH(Register.getR(r)));
            compiler.getStackManager().useTemp(1);
        }

        if (callSuperInit) {
            String sname = superDef.getType().getName().getName();

            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
            compiler.addInstruction(new PUSH(Register.R0));
            compiler.getStackManager().useTemp(1);

            compiler.addInstruction(new BSR(new Label("init." + sname)));

            compiler.addInstruction(new POP(Register.R0));
            compiler.getStackManager().releaseTemp(1);
        }

        classFields.codeGenInitFields(compiler, classDef);

        for (int r = last; r >= first; r--) {
            compiler.addInstruction(new POP(Register.getR(r)));
            compiler.getStackManager().releaseTemp(1);
        }

        compiler.addInstruction(new RTS());

        int d = compiler.getStackManager().getTSTOForLocals();
        tstoLine.setInstruction(new TSTO(new ImmediateInteger(d)));

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


    public void codeGenByteClass(DecacCompiler compiler) {
        String className = this.classDefinition.getType().getName().getName();

        String superName = this.classDefinition.getSuperClass() == null
            ? "java/lang/Object"
            : this.classDefinition.getSuperClass().getType().getName().getName();

        compiler.getByteManager().startClass(className, ByteManager.toInternalClassName(superName));
        

        // fields
        for (AbstractDeclField f : classFields.getList()) {
            DeclField df = (DeclField) f;
            String fname = df.getFieldName().getName().getName();
            String desc = ByteManager.typeToDescriptor(df.getFieldName().getDefinition().getType());
            String vis = (df.getVisibility() == Visibility.PROTECTED) ? "protected" : "public";
            compiler.getByteManager().getInstructions().add(".field " + vis + " " + fname + " " + desc);
        }
        compiler.getByteManager().getInstructions().add("");

        compiler.getByteManager().getInstructions().add(".method public <init>()V");
        compiler.getByteManager().getInstructions().add(".limit stack 0");
        compiler.getByteManager().getInstructions().add(".limit locals 1");

        compiler.getByteManager().enterMethod();
        compiler.resetLocalSlots(1); // 0 = this

        compiler.getByteManager().emitALoad(0);
        compiler.getByteManager().emitInvokeSpecial(ByteManager.toInternalClassName(superName), "<init>", "()V");

        for (AbstractDeclField f : classFields.getList()) {
            DeclField df = (DeclField) f;
            if (df.hasExplicitInitialization()) {
                compiler.getByteManager().emitALoad(0);
                df.codeGenByteInit(compiler);
                String fname = df.getFieldName().getName().getName();
                String desc = ByteManager.typeToDescriptor(df.getFieldName().getDefinition().getType());
                compiler.getByteManager().emitPutField(ByteManager.toInternalClassName(className), fname, desc);
            }
        }

        compiler.getByteManager().emitReturnVoid();

        int stack = compiler.getByteManager().getMaxStack();
        // Patch limits (lines right after the constructor .method)
        java.util.List<String> ins = compiler.getByteManager().getInstructions();
        int methodLine = -1;
        for (int i = ins.size() - 1; i >= 0; i--) {
            if (ins.get(i).startsWith(".method public <init>()V")) { methodLine = i; break; }
        }
        if (methodLine >= 0) {
            ins.set(methodLine + 1, ".limit stack " + stack);
            ins.set(methodLine + 2, ".limit locals 1");
        }
        compiler.getByteManager().getInstructions().add(".end method");
        compiler.getByteManager().getInstructions().add("");

        // methods
        this.classMethods.codeGenListDeclMethodByte(compiler, className);
    }
}
