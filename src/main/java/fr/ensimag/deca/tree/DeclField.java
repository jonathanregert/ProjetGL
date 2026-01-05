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

    public DeclField(Visibility visibility, AbstractIdentifier type, AbstractIdentifier fieldName, AbstractInitialization initialization) {
        Validate.notNull(visibility);
        Validate.notNull(type);
        Validate.notNull(fieldName);
        Validate.notNull(initialization);
        this.visibility = visibility;
        this.type = type;
        this.fieldName = fieldName;
        this.initialization = initialization;
    }


    @Override
    protected void verifyDeclField(DecacCompiler compiler, ClassDefinition currentClass)
        throws ContextualError {
    
    Type t = type.verifyType(compiler);
    if (t.isVoid()) {
        throw new ContextualError("Un champ ne peut pas être de type void", type.getLocation());
    }

    Symbol name = fieldName.getName();

    // 3. Gestion de l'index (Position dans l'objet en mémoire)
    // L'index commence après les champs de la super-classe
    int index = currentClass.getNumberOfFields() + 1;
    currentClass.setNumberOfFields(index);

    // 4. Création de la définition (FieldDefinition)
    // On précise : type, location, visibilité, la classe parente, et l'index
    FieldDefinition fieldDef = new FieldDefinition(t, fieldName.getLocation(), 
                                                   visibility, currentClass, index);


    // Debug
    System.out.println("Test champ: " + name + " dans " + currentClass.getType());
    System.out.println("Existe déjà localement ? " + (currentClass.getMembers().get(name) != null));
    System.out.println("DEBUG PASSE 2: Déclaration de " + name);
    System.out.println("DEBUG PASSE 3: Init de " + name);
    
    // 5. Ajout à l'environnement des membres de la classe (env_expr)
    try {
        currentClass.getMembers().declare(name, fieldDef);
    } catch (EnvironmentExp.DoubleDefException e) {
        throw new ContextualError("Double définition du champ " + name, fieldName.getLocation());
    }

    // 6. Décoration de l'identificateur
    this.fieldName.setDefinition(fieldDef);

}


    
    @Override
    public void decompile(IndentPrintStream s) {
        // not yet implemented
        throw new UnsupportedOperationException("Not yet implemented");
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
