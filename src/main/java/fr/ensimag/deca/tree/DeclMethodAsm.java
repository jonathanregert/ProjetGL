package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import org.apache.commons.lang.Validate;

/**
 * @author gl42
 * @date 01/01/2026
 */
public class DeclMethodAsm extends AbstractDeclMethod {

    
    private final AbstractIdentifier type;
    private final AbstractIdentifier methodName;
    private final ListDeclParam params;
    private final String code;

    public DeclMethodAsm(AbstractIdentifier type, AbstractIdentifier methodName, ListDeclParam params, String code) {
        Validate.notNull(type);
        Validate.notNull(methodName);
        Validate.notNull(params);
        Validate.notNull(code);
        this.type = type;
        this.methodName = methodName;
        this.params = params;
        this.code = code;
    }

    @Override
    protected void verifyDeclMethod(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        
        // Type t = type.verifyType(compiler);
        // params.verifyListDeclVariable(compiler, localEnv, currentClass);
   //manque verify de code (minimal)
        
    }

    

    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(") asm(\"");
        s.print(code);
        s.print("\");");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        methodName.iter(f);
        params.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, true);
    }

    @Override
    public void codeGenDeclMethod(DecacCompiler compiler) {
        
    }

    @Override
    public AbstractIdentifier getMethodName()
    {
        return methodName;
    }

    @Override
    protected  void verifyMethodBody(DecacCompiler compiler, 
        EnvironmentExp envExp, // L'environnement global
        ClassDefinition currentClass, 
        Type returnType)
            throws ContextualError{};
}