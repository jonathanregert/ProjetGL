package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassType;
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
        // Labels uniques
        int id = compiler.getLabelId();
        Label lFalse = new Label("instanceof_false_" + id);
        Label lTrue  = new Label("instanceof_true_" + id);
        Label lLoop  = new Label("instanceof_loop_" + id);
        Label lEnd   = new Label("instanceof_end_" + id);

        // 1) évaluer expr -> robj
        expr.codeGenExpr(compiler, robj);

        // 2) si null => false
        compiler.addInstruction(new CMP(new NullOperand(), robj));
        compiler.addInstruction(new BEQ(lFalse));

        // 3) rvt = vtable(obj) = 0(robj)
        compiler.addInstruction(new LOAD(new RegisterOffset(0, robj), rvt));

        // Adresse vtable cible (connue à la compilation)
        // typeName.verifyType() a décoré typeName en ClassDefinition via ClassType
        ClassDefinition targetDef = typeName.getClassDefinition();
        RegisterOffset targetVTableAddr = targetDef.getAddrTable();

        // 4) boucle sur la hiérarchie des vtables
        compiler.addLabelToBlock(lLoop);

        // si rvt == null => false
        compiler.addInstruction(new CMP(new NullOperand(), rvt));
        compiler.addInstruction(new BEQ(lFalse));

        // si rvt == &vtable(cible) => true
        compiler.addInstruction(new CMP(targetVTableAddr, rvt));
        compiler.addInstruction(new BEQ(lTrue));

        // rvt = vtable(parent) = 0(rvt)
        compiler.addInstruction(new LOAD(new RegisterOffset(0, rvt), rvt));
        compiler.addInstruction(new BRA(lLoop));

        // true
        compiler.addLabelToBlock(lTrue);
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), robj));
        compiler.addInstruction(new BRA(lEnd));

        // false
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