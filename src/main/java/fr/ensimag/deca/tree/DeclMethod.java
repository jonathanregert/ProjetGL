package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorManager.RuntimeError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

import java.io.PrintStream;
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
    private final MethodBody body;

    public DeclMethod(AbstractIdentifier returnType, AbstractIdentifier methodName, ListDeclParam params, MethodBody body) {
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
        
        // Type de retour
        Type t = returnType.verifyType(compiler);
        // Signature
        Signature sig = this.params.verifyListDeclParam(compiler);

        // Méthode de même nom dans la super-classe(si elle existe)
        MethodDefinition superMethod = null;
        if (currentClass.getSuperClass() != null){
            if (currentClass.getSuperClass().getMembers().get(methodName.getName()) != null){
                superMethod = currentClass.getSuperClass().getMembers()
                        .get(methodName.getName()).asMethodDefinition(
                            "Le membre " + methodName.getName() + " n'est pas une méthode",
                            getLocation()
                        );
            }
        }

        int index;
        if (superMethod != null){
            // Override = mêmes contraintes
            if (!sig.equals(superMethod.getSignature())){
                throw new ContextualError("Signature incompatible avec la méthode de la super-classe.", getLocation());
            }
            if (!t.sameType(superMethod.getType())){
                throw new ContextualError("Type de retour incompatible avec la méthode de la super-classe.", getLocation());
            }
            index = superMethod.getIndex();
        } else {
            // Nouvelle méthode : nouvel index
            index = currentClass.incNumberOfMethods() - 1;
        }

        // les conditions :
        // 2.7 cas du override :
        // MethodDefinition superMethod = (MethodDefinition) currentClass.getSuperClass()
        //                             .getMembers().get(methodName.getName());

        // if (superMethod != null) { // il y a override
        // // Vérifier que sig == sig2
        // if (!sig.equals(superMethod.getSignature())) {
        //     throw new ContextualError("Signature pas convenable en override avec la super classe", getLocation());
        // }
        // // Vérifier subtype(env_types, type, type2)
        // if (!t.sameType(superMethod.getType())) {
        //     throw new ContextualError("Return type incompatible avec la super méthode", getLocation());
        // }
        // }

        MethodDefinition methodDef = new MethodDefinition(t, getLocation(), sig, index);

        String className = currentClass.getType().getName().getName();
        String methodeName = methodName.getName().getName();
        methodDef.setLabel(new Label("code." + className + "." + methodeName));

        try {
            currentClass.getMembers().declare(methodName.getName(), methodDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Méthode " + methodName.getName() + " déjà définie", 
                    methodName.getLocation());
        }
        
        // Décoration
        methodName.setDefinition(methodDef);
        
    }

    @Override
    protected void verifyMethodBody(DecacCompiler compiler, 
        EnvironmentExp envExp,
        ClassDefinition currentClass, 
        Type returnType) throws ContextualError {

    // 3.12

    EnvironmentExp envExpParams = new EnvironmentExp(envExp);
    // Debug
    // System.out.println("Env exp avant params méthode " + methodName.getName() + " :\n" + envExp.toString());

    this.params.verifyListDeclParam(compiler, envExpParams);
    this.body.getVars().verifyListDeclVariable(compiler, envExpParams, currentClass);
    this.body.getInsts().verifyListInst(compiler, envExpParams, currentClass, returnType);
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
        body.iterChildren(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        returnType.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, false);
        body.prettyPrintChildren(s, prefix);
    }

    @Override
    public void codeGenDeclMethod(DecacCompiler compiler) {
        MethodDefinition md = methodName.getMethodDefinition();
        Label start = md.getLabel();

        String className = compiler.getCurrentClassName();
        if (className == null) className = "UnknownClass";
        String mName = methodName.getName().getName();
        Label end = new Label("fin." + className + "." + mName);

        Label pilePleine = compiler.getErrorManager().label(RuntimeError.STACK_OVERFLOW);

        compiler.addLabel(start);
        compiler.addComment("Méthode " + className + "." + mName);

        compiler.getRegAllocator().reset();
        compiler.getStackManager().enterBlock();

        compiler.setCurrentMethodEndLabel(end);

        // On bufferise juste pour insérer TSTO/BOV avant les instructions du corps
        compiler.beginBlock();

        // Corps (à terme: utiliser addToBlock dans les inst, mais OK pour MVP)
        body.getInsts().codeGenListInst(compiler);

        // Saut fin (si on sort sans return, on tombe ici)
        // (Poly: ERROR si non-void, on le fera après)
        compiler.addToBlock(new RTS());

        int d = compiler.getStackManager().getTSTOForLocals();
        compiler.addFirstToBlock(new TSTO(new ImmediateInteger(d)));
        compiler.addFirstToBlock(new BOV(pilePleine));

        compiler.endBlock();

        // Label de fin après le bloc (pour que les return BRA end marchent)
        compiler.addLabel(end);
        compiler.addInstruction(new RTS());

        compiler.setCurrentMethodEndLabel(null);
        compiler.getStackManager().exitBlock();
    }
    @Override
    public AbstractIdentifier getMethodName()
    {
        return methodName;
    }


}