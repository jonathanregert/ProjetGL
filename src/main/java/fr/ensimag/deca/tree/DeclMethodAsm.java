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
    private final MethodAsmBody code;

    public DeclMethodAsm(AbstractIdentifier type, AbstractIdentifier methodName, ListDeclParam params, MethodAsmBody code) {
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
    protected void verifyMethodBody(DecacCompiler compiler,
                                    EnvironmentExp envExp,
                                    ClassDefinition currentClass,
                                    Type returnType)
            throws ContextualError {
        
        EnvironmentExp envExpParams = new EnvironmentExp(envExp);
        this.params.verifyListDeclParam(compiler, envExpParams);
        code.verifyMethodAsmBody(compiler, envExp, currentClass);
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(") asm(\"");
        code.decompile(s);
        s.print("\");");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        methodName.iter(f);
        params.iter(f);
        code.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, true);
        code.prettyPrint(s, prefix, true);
    }

    @Override
    public void codeGenDeclMethod(DecacCompiler compiler) {
        MethodDefinition md = methodName.getMethodDefinition();

        compiler.addLabel(md.getLabel());
        compiler.addComment("Méthode ASM " + md.getLabel());

        String rawAsm = code.getAsmText().getValue();

        if (rawAsm.startsWith("\"") && rawAsm.endsWith("\"") && rawAsm.length() >= 2) {
            rawAsm = rawAsm.substring(1, rawAsm.length() - 1);
        }

        rawAsm = rawAsm.replace("\\n", "\n");

        rawAsm = rawAsm.replace("\\r", "\r");

        rawAsm = rawAsm.replace("\\\"", "\"");

        rawAsm = rawAsm.replace("\\\\", "\\");

        String[] lines = rawAsm.split("\\R");

        for (String l : lines) {
            String trimmed = l.trim();
            if (!trimmed.isEmpty()) {
                compiler.addInline(trimmed);
            }
        }
        if (!containsRTS(rawAsm)) {
            compiler.addInstruction(new RTS());
        }
    }


    // helper local
    private boolean containsRTS(String rawAsm) {
        String[] lines = rawAsm.split("\\R");
        for (String l : lines) {
            String s = l.trim().toUpperCase();
            if (s.isEmpty()) continue;
            int semicolon = s.indexOf(';');
            if (semicolon >= 0) s = s.substring(0, semicolon).trim();
            if (s.equals("RTS")) return true;
        }
        return false;
    }

    @Override
    public AbstractIdentifier getMethodName()
    {
        return methodName;
    }
}