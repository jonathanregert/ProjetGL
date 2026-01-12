package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.instructions.RTS;

import java.io.PrintStream;
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

        Type t = type.verifyType(compiler);
        Signature sig = params.verifyListDeclParam(compiler);

        MethodDefinition superMethod = null;
        if (currentClass.getSuperClass() != null) {
            if (currentClass.getSuperClass().getMembers().get(methodName.getName()) != null) {
                superMethod = currentClass.getSuperClass().getMembers()
                        .get(methodName.getName())
                        .asMethodDefinition(
                            "Le membre " + methodName.getName() + " n'est pas une méthode",
                            getLocation()
                        );
            }
        }
        int index;
        if (superMethod != null) {
            if (!sig.equals(superMethod.getSignature())) {
                throw new ContextualError("Signature incompatible avec la méthode de la super-classe",
                        getLocation());
            }
            if (!t.sameType(superMethod.getType())) {
                throw new ContextualError("Type de retour incompatible avec la méthode de la super-classe",
                        getLocation());
            }
            index = superMethod.getIndex();
        } else {
            index = currentClass.incNumberOfMethods() - 1;
        }

        MethodDefinition methodDef = new MethodDefinition(t, getLocation(), sig, index);

        String classNameStr = currentClass.getType().getName().getName();
        String methodNameStr = methodName.getName().getName();
        methodDef.setLabel(new Label("code." + classNameStr + "." + methodNameStr));

        try {
            currentClass.getMembers().declare(methodName.getName(), methodDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Méthode " + methodName.getName() + " déjà définie",
                    methodName.getLocation());
        }
        methodName.setDefinition(methodDef);
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
        MethodDefinition md = methodName.getMethodDefinition();
        compiler.addLabel(md.getLabel());
        compiler.addComment("Méthode ASM " + md.getLabel());

        String[] lines = code.split("\\R");
        for (String l : lines){
            String trimmed = l.trim();
            if (!trimmed.isEmpty()){
                compiler.add(new Line(trimmed));
            }
        }

        String codeTrim = code.trim().toUpperCase();
        if (!codeTrim.endsWith("RTS") && !codeTrim.contains("\nRTS") && !codeTrim.contains("\rRTS")){
            compiler.addInstruction(new RTS());
        }
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