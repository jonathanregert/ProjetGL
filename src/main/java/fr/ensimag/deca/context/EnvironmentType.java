package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import java.util.HashMap;
import java.util.Map;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.ListDeclField;
import fr.ensimag.deca.tree.Location;

// A FAIRE: étendre cette classe pour traiter la partie "avec objet" de Déca
/**
 * Environment containing types. Initially contains predefined identifiers, more
 * classes can be added with declareClass().
 *
 * @author gl42
 * @date 01/01/2026
 */
public class EnvironmentType {
    public EnvironmentType(DecacCompiler compiler) {
        
        envTypes = new HashMap<Symbol, TypeDefinition>();
        
        Symbol intSymb = compiler.createSymbol("int");
        INT = new IntType(intSymb);
        envTypes.put(intSymb, new TypeDefinition(INT, Location.BUILTIN));

        Symbol floatSymb = compiler.createSymbol("float");
        FLOAT = new FloatType(floatSymb);
        envTypes.put(floatSymb, new TypeDefinition(FLOAT, Location.BUILTIN));

        Symbol voidSymb = compiler.createSymbol("void");
        VOID = new VoidType(voidSymb);
        envTypes.put(voidSymb, new TypeDefinition(VOID, Location.BUILTIN));

        Symbol booleanSymb = compiler.createSymbol("boolean");
        BOOLEAN = new BooleanType(booleanSymb);
        envTypes.put(booleanSymb, new TypeDefinition(BOOLEAN, Location.BUILTIN));

        Symbol stringSymb = compiler.createSymbol("string");
        STRING = new StringType(stringSymb);
        // not added to envTypes, it's not visible for the user.


        // Objet pour les classes
        Symbol objectSymb = compiler.createSymbol("Object");
        this.OBJECT = new ClassType(objectSymb, Location.BUILTIN, null);
        ClassDefinition objectDef = this.OBJECT.getDefinition();
        envTypes.put(objectSymb, objectDef);
        
    }

    private final Map<Symbol, TypeDefinition> envTypes;

    public TypeDefinition defOfType(Symbol s) {
        return envTypes.get(s);
    }

    public final VoidType    VOID;
    public final IntType     INT;
    public final FloatType   FLOAT;
    public final StringType  STRING;
    public final BooleanType BOOLEAN;
    public final ClassType OBJECT;

    public boolean isDeclared(Symbol name) {
        return envTypes.containsKey(name);
    }

    public TypeDefinition get(Symbol s) {
        return envTypes.get(s);
    }

    public void declare(Symbol name, TypeDefinition typeDef) throws DoubleDefException {
        if (isDeclared(name)) {
            throw new DoubleDefException("Type " + name + " déjà déclaré");
        }
        envTypes.put(name, typeDef);
    }

    //public void declareClass(ClassType classType) throws DoubleDefException {
      //  if (isDeclared(classType.getName())) {
           // throw new DoubleDefException("Type " + classType.getName() + " déjà déclaré");
      //  }
      //  declare(classType.getName(), classType.getDefinition());
    //}

    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = 1L; // eviter le warning :  has no definition of serialVersionUID
        public DoubleDefException(String message) {
            super(message);
        }
    }
}

