package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.TreeFunction;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.context.FieldDefinition;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * @author gl42
 * @date 01/01/2026
 */
public class DeclField extends AbstractDeclField {

    
    final private Visibility visibility;
    final private AbstractIdentifier type;
    final private AbstractIdentifier fieldName;
    final private AbstractInitialization initialization;
    final private boolean isFinal;

    public DeclField(Visibility visibility, AbstractIdentifier type, AbstractIdentifier fieldName, AbstractInitialization initialization, boolean isFinal) {
        Validate.notNull(visibility);
        Validate.notNull(type);
        Validate.notNull(fieldName);
        Validate.notNull(initialization);
        this.visibility = visibility;
        this.isFinal = isFinal;
        this.type = type;
        this.fieldName = fieldName;
        this.initialization = initialization;
    }

    public Visibility getVisibility() { return visibility; }

    @Override
    protected void verifyDeclField(DecacCompiler compiler, ClassDefinition currentClass)
        throws ContextualError {
    
    Type t = type.verifyType(compiler);
    if (t.isVoid()) {
        throw new ContextualError("Un champ ne peut pas être de type void", type.getLocation());
    }

    Symbol name = fieldName.getName();

    // index du champ incrémenté
    int index = currentClass.getNumberOfFields() + 1;
    currentClass.setNumberOfFields(index);

    // si ca existe déjà dans la classe courante ou héritée
    if (currentClass.getMembers().get(name) != null) {
        throw new ContextualError("Le champ " + name + " est déjà défini dans cette classe ou hérité", 
                fieldName.getLocation());
    }

    // fieldDef : type, nom, visibilité, classe courante, index
    FieldDefinition fieldDef = new FieldDefinition(t, fieldName.getLocation(), 
                                                   visibility, currentClass, index, this.isFinal);

    
    // + environnement des membres de la classe
    try {
        currentClass.getMembers().declare(name, fieldDef);
    } catch (EnvironmentExp.DoubleDefException e) {
        // jamais arrivé ici, mais pour sécuriser
        throw new ContextualError("Double définition du champ " + name, fieldName.getLocation());
    }

    // deco de l'identifier
    this.fieldName.setDefinition(fieldDef);

    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        if (visibility == Visibility.PROTECTED) {
            s.print("protected ");
        }
        type.decompile(s);
        s.print(" ");
        fieldName.decompile(s);
        initialization.decompile(s);
        s.println(";");    
    }

    @Override
    protected void codeGenDeclField(DecacCompiler compiler){
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected AbstractIdentifier getFieldName(){
        return fieldName;
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        fieldName.iter(f);
        initialization.iter(f);
    }

    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        s.print(prefix + "[" + getLocation() + "] [visibility=" + visibility + "] DeclField \n");
        type.prettyPrint(s, prefix, false);
        fieldName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);

    }

    @Override
    protected void verifyFieldInitialization(DecacCompiler compiler,
            ClassDefinition currentClass) throws ContextualError {
        Type t = fieldName.getDefinition().getType();
        this.initialization.verifyInitialization(compiler, t, currentClass.getMembers(), currentClass);
            }

    @Override
    protected void codeGenInitField(DecacCompiler compiler, ClassDefinition currentClass) {
        FieldDefinition fd = fieldName.getFieldDefinition();
        int fieldOffset = fd.getIndex();
        Type t = fd.getType();

        // --- init explicite ---
        if (initialization instanceof Initialization) {
            // this -> R2, puis sauvegarde
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(2)));
            compiler.addInstruction(new PUSH(Register.getR(2)));
            compiler.getStackManager().useTemp(1);

            // registre où calculer la valeur
            GPRegister r = compiler.getRegAllocator().alloc();
            if (r == null) {
                // fallback minimal : choisir un GPRegister "scratch" autorisé (souvent R1)
                r = Register.getR(1);
            }

            // calcule la valeur (peut écraser R2)
            ((Initialization) initialization).codeGenValue(compiler, r);

            // restaure this
            compiler.addInstruction(new POP(Register.getR(2)));
            compiler.getStackManager().releaseTemp(1);

            // store dans le champ
            compiler.addInstruction(new STORE(r, new RegisterOffset(fieldOffset, Register.getR(2))));

            // libérer si alloué
            if (r != Register.getR(1)) { // ou mieux: si tu sais distinguer "alloué" vs "fallback"
                compiler.getRegAllocator().free(r);
            }
            return;
        }

        // --- valeur par défaut ---
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(2)));

        if (t.isClass()) {
            compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        } else {
            compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.R0));
        }
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(fieldOffset, Register.getR(2))));
    }
}