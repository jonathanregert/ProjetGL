package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.TreeFunction;
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


    // Debug
    // System.out.println("Test champ: " + name + " dans " + currentClass.getType());
    // System.out.println("Existe déjà localement ? " + (currentClass.getMembers().get(name) != null));
    // System.out.println("DEBUG PASSE 2: Déclaration de " + name);
    // System.out.println("DEBUG PASSE 3: Init de " + name);
    
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

}
