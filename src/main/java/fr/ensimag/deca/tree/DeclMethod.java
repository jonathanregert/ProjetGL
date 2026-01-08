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
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.MethodDefinition;
import org.apache.commons.lang.Validate;

/**
 * @author gl42
 * @date 01/01/2026
 */
public class DeclMethod extends AbstractDeclMethod {

    
    private final AbstractIdentifier returnType;
    private final AbstractIdentifier methodName;
    private final ListDeclParam params;
    private final ListInst body;

    public DeclMethod(AbstractIdentifier returnType, AbstractIdentifier methodName, ListDeclParam params, ListInst body) {
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
    protected void verifyDeclMethod(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        
        // on recup le type
        Type t = returnType.verifyType(compiler);
        // on recup la sig
        Signature sig = this.params.verifyListDeclParam(compiler);

        // les conditions :
        // 2.7 cas du override :
        MethodDefinition superMethod = (MethodDefinition) currentClass.getSuperClass()
                                    .getMembers().get(methodName.getName());

        if (superMethod != null) { // il y a override
        // Vérifier que sig == sig2
        if (!sig.equals(superMethod.getSignature())) {
            throw new ContextualError("Signature pas convenable en override avec la super classe", getLocation());
        }
        // Vérifier subtype(env_types, type, type2)
        if (!t.sameType(superMethod.getType())) {
            throw new ContextualError("Return type incompatible avec la super méthode", getLocation());
        }
        }

        MethodDefinition methodDef = new MethodDefinition(t, getLocation(), sig, 0);
        try {
            currentClass.getMembers().declare(methodName.getName(), methodDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Méthode " + methodName.getName() + " déjà définie", 
                    methodName.getLocation());
        }
        
        // Deco
        methodName.setDefinition(methodDef);
        
    }

    @Override
    protected void verifyMethodBody(DecacCompiler compiler, 
        EnvironmentExp envExp,
        ClassDefinition currentClass, 
        Type returnType) throws ContextualError {

    // 3.12

    EnvironmentExp envExpParams = new EnvironmentExp(envExp);
    
    this.params.verifyListDeclParam(compiler, envExpParams);
    this.body.verifyListInst(compiler, envExpParams, currentClass, returnType);
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