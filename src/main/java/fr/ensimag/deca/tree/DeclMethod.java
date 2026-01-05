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
public class DeclMethod extends AbstractDeclMethod {

    
    private final AbstractIdentifier returnType;
    private final AbstractIdentifier methodName;
    private final ListDeclVar params;
    private final ListInst body;

    public DeclMethod(AbstractIdentifier returnType, AbstractIdentifier methodName, ListDeclVar params, ListInst body) {
        Validate.notNull(returnType);
        Validate.notNull(methodName);
        Validate.notNull(params);
        Validate.notNull(body);
        this.returnType = returnType;
        this.methodName = methodName;
        this.params = params;
        this.body = body;
    }

    @Override
    protected void verifyDeclMethod(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        
        Type t = returnType.verifyType(compiler);
        params.verifyListDeclVariable(compiler, localEnv, currentClass);
        body.verifyListInst(compiler, localEnv, currentClass, t);
        
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        returnType.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(") ");
        body.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        returnType.iter(f);
        methodName.iter(f);
        params.iter(f);
        body.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        returnType.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

    @Override
    public void codeGenDeclMethod(DecacCompiler compiler) {
        
    }

    @Override
    public AbstractIdentifier getMethodName()
    {
        return methodName;
    }


}