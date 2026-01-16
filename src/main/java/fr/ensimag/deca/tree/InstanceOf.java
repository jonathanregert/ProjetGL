package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

public class InstanceOf extends AbstractExpr {
    private AbstractExpr expr;
    private AbstractIdentifier typeName;

    public InstanceOf(AbstractExpr expr, AbstractIdentifier typeName) {
        Validate.notNull(expr);
        Validate.notNull(typeName);
        this.expr = expr;
        this.typeName = typeName;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);
        Type cibleType = typeName.verifyType(compiler);

        if (!exprType.isClass() && !exprType.isNull()) {
            throw new ContextualError("L'opérateur instanceof s'applique à un objet, pas à " + exprType,
                expr.getLocation());
        }
        if (!cibleType.isClass()) {
            throw new ContextualError("Le deuxième argument d'instanceof doit être une classe",
                typeName.getLocation());
        }

        typeName.setType(cibleType);

        Type boolType = compiler.environmentType.BOOLEAN;
        this.setType(boolType);
        return boolType;
    }


    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        GPRegister robj = target;
        GPRegister rvt  = Register.getR(1);

        int id = compiler.getLabelId();
        Label lFalse = new Label("instanceof_false_" + id);
        Label lTrue  = new Label("instanceof_true_" + id);
        Label lLoop  = new Label("instanceof_loop_" + id);
        Label lEnd   = new Label("instanceof_end_" + id);

        expr.codeGenExpr(compiler, robj);

        compiler.addInstruction(new CMP(new NullOperand(), robj));
        compiler.addInstruction(new BEQ(lFalse));

        compiler.addInstruction(new LOAD(new RegisterOffset(0, robj), rvt));

        ClassDefinition targetDef = typeName.getClassDefinition();
        RegisterOffset targetVTableAddr = targetDef.getAddrTable();

        compiler.addLabelToBlock(lLoop);

        compiler.addInstruction(new CMP(new NullOperand(), rvt));
        compiler.addInstruction(new BEQ(lFalse));

        compiler.addInstruction(new CMP(targetVTableAddr, rvt));
        compiler.addInstruction(new BEQ(lTrue));

        compiler.addInstruction(new LOAD(new RegisterOffset(0, rvt), rvt));
        compiler.addInstruction(new BRA(lLoop));

        compiler.addLabelToBlock(lTrue);
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), robj));
        compiler.addInstruction(new BRA(lEnd));

        compiler.addLabelToBlock(lFalse);
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), robj));

        compiler.addLabelToBlock(lEnd);
    }


    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(" instanceof ");
        typeName.decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        typeName.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        typeName.prettyPrint(s, prefix, true);
    }
}