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
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

import java.io.PrintStream;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.codegen.ByteManager;

import org.apache.commons.lang.Validate;
import java.util.*;

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
            // Override = mêmes contraintes (2.7)
            if (!t.isSubtype(compiler.environmentType, superMethod.getType())){
                throw new ContextualError("Type de retour incompatible avec la méthode de la super-classe.", getLocation());
            }
            if (!sig.equals(superMethod.getSignature())){
                // Debug
                // System.out.println("Sig actuelle : " + sig.toString());
                // System.out.println("Sig super : " + superMethod.getSignature().toString());
                throw new ContextualError("Signature incompatible avec la méthode de la super-classe.", getLocation());
            }
            index = superMethod.getIndex();
        } else {
            // Nouvelle méthode : nouvel index
            index = currentClass.incNumberOfMethods() - 1;
        }
        // System.err.println("[P2] Method " + currentClass.getType().getName().getName()
        //     + "." + methodName.getName().getName()
        //     + " index=" + index
        //     + " super=" + (superMethod != null));

    

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

        EnvironmentExp envExpParams = new EnvironmentExp(envExp);

        // Déclarer les paramètres dans l'environnement local de la méthode
        this.params.verifyListDeclParam(compiler, envExpParams);

        // Variables locales (tu feras aussi le même check local↔champ dans verifyListDeclVariable)
        this.body.getVars().verifyListDeclVariable(compiler, envExpParams, currentClass);

        // Instructions
        this.body.getInsts().verifyListInst(compiler, envExpParams, currentClass, returnType);

        // Non-void doit contenir un return
        if (!returnType.sameType(compiler.environmentType.VOID) && !body.getInsts().containsReturn()) {
            throw new ContextualError("méthode non void sans instruction return", getLocation());
        }
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
        body.prettyPrint(s, prefix, true);
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

        int k = 3;
        for (AbstractDeclParam p : params.getList()) {
            DeclParam dp = (DeclParam) p;
            ParamDefinition pd = (ParamDefinition) dp.getVarName().getDefinition();
            pd.setOperand(new RegisterOffset(-k, Register.LB));
            k++;
        }

        Line tstoLine = new Line(new TSTO(new ImmediateInteger(0)));
        compiler.add(tstoLine);
        compiler.addInstruction(new BOV(pilePleine));

        int first = compiler.getRegAllocator().getFirstAlloc();
        int last  = compiler.getRegAllocator().getMaxReg();
        for (int r = first; r <= last; r++) {
            compiler.addInstruction(new PUSH(Register.getR(r)));
            compiler.getStackManager().useTemp(1);
        }

        body.getVars().codeGenListDeclVar(compiler);
        body.getInsts().codeGenListInst(compiler);

        if (md.getType().isVoid()) {
            compiler.addInstruction(new BRA(end));
        }

        compiler.addLabel(end);

        for (int r = last; r >= first; r--) {
            compiler.addInstruction(new POP(Register.getR(r)));
            compiler.getStackManager().releaseTemp(1);
        }

        compiler.addInstruction(new RTS());

        int d = compiler.getStackManager().getTSTOForLocals();
        tstoLine.setInstruction(new TSTO(new ImmediateInteger(d)));

        compiler.setCurrentMethodEndLabel(null);
        compiler.getStackManager().exitBlock();
    }

    @Override
    public AbstractIdentifier getMethodName()
    {
        return methodName;
    }

    public void codeGenByteDeclMethod(DecacCompiler compiler, String ownerClassName) {
        MethodDefinition md = methodName.getMethodDefinition();
        String mName = methodName.getName().getName();

        Signature sig = md.getSignature();
        StringBuilder desc = new StringBuilder();
        desc.append("(");
        for (int i = 0; i < sig.size(); i++) {
            desc.append(ByteManager.typeToDescriptor(sig.paramNumber(i)));
        }
        desc.append(")");
        desc.append(ByteManager.typeToDescriptor(md.getType()));

        compiler.getByteManager().getInstructions().add(".method public " + mName + desc);
        compiler.getByteManager().getInstructions().add(".limit stack 0");
        compiler.getByteManager().getInstructions().add(".limit locals 0");

        compiler.getByteManager().enterMethod();
        compiler.resetLocalSlots(1); // 0: this

        // allocate JVM slots for params in order
        for (AbstractDeclParam p : params.getList()) {
            DeclParam dp = (DeclParam) p;
            ParamDefinition pd = (ParamDefinition) dp.getVarName().getDefinition();
            compiler.allocLocalSlot(pd);
        }

        // locals
        body.getVars().codeGenListDeclVarByte(compiler);

        // body
        body.getInsts().codeGenListInstByte(compiler);

        // implicit return for void if not already returned
        if (md.getType().isVoid()) {
            compiler.getByteManager().emitReturnVoid();
        }

        int stack = compiler.getByteManager().getMaxStack();
        int locals = compiler.getMaxLocalSlot();
        compiler.getByteManager().getInstructions().set(
            compiler.getByteManager().getInstructions().size() - 1 - 1 - 0, // no-op: keep structure stable
            compiler.getByteManager().getInstructions().get(compiler.getByteManager().getInstructions().size() - 1 - 1 - 0)
        );
        int idxMethod = compiler.getByteManager().getInstructions().size();

        int methodLine = -1;
        List<String> ins = compiler.getByteManager().getInstructions();
        for (int i = ins.size() - 1; i >= 0; i--) {
            if (ins.get(i).startsWith(".method ")) { methodLine = i; break; }
        }
        if (methodLine >= 0) {
            ins.set(methodLine + 1, ".limit stack " + stack);
            ins.set(methodLine + 2, ".limit locals " + locals);
        }

        compiler.getByteManager().getInstructions().add(".end method");
        compiler.getByteManager().getInstructions().add("");
    }
}