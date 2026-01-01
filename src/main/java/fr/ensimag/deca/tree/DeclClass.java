package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.ClassDefinition;
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

        // La classe elle meme n'existe pas déjà
        if (compiler.environmentType.isDeclared(ClassName.getName())) {
            throw new ContextualError(
                "Classe " + ClassName.getName() + " déjà déclarée", 
                getLocation()
            );
        }

        TypeDefinition superClassDef = compiler.environmentType.get(ClassExtention.getName());
        if (superClassDef == null || !superClassDef.getType().isClass()) {
            throw new ContextualError(
                "La super-classe " + ClassExtention.getName() + " n'est pas une classe", 
                getLocation()
            );
        }

        ClassType newClassType = new ClassType(ClassName.getName(), ClassName.getLocation(), (fr.ensimag.deca.context.ClassDefinition) superClassDef);
        ClassDefinition newClassDef = newClassType.getDefinition();

        try {
        // On l'ajoute à l'environnement global des types
        compiler.environmentType.declare(getName(), newClassDef);
        } catch (EnvironmentType.DoubleDefException e) {
        throw new ContextualError("Double définition de la classe " + ClassName.getName(), ClassName.getLocation());
        }

        ClassName.setDefinition(newClassDef); // deco        

        }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }
    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        throw new UnsupportedOperationException("Not yet supported");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        throw new UnsupportedOperationException("Not yet supported");
    }

}
