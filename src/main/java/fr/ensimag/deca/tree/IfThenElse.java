package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;


/**
 * Full if/else if/else statement.
 *
 * @author gl42
 * @date 01/01/2026
 */
public class IfThenElse extends AbstractInst {
    
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public void setElseBranch(ListInst elseBranch) {
        org.apache.commons.lang.Validate.notNull(elseBranch);
        this.elseBranch = elseBranch;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        // Vérification de la condition
        Type condType = condition.verifyExpr(compiler, localEnv, currentClass);
        if (!condType.isBoolean()) {
            throw new ContextualError("La condition d'une instruction if doit être de type boolean.", condition.getLocation());
        }
        // Vérification du "then"
        thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        // Vérification du "else"
        elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler){
        int id = compiler.getLabelId();
        Label labelElse = new Label("else_" + id);
        Label labelEnd = new Label("endif_" + id);

        GPRegister rCond = compiler.getRegAllocator().alloc();
        if (rCond == null){
            rCond = Register.R0;
        }
        condition.codeGenExpr(compiler, rCond);

        compiler.addInstruction(new CMP(new ImmediateInteger(0), rCond));
        compiler.addInstruction(new BEQ(labelElse));

        if (!rCond.equals(Register.R0)){
            compiler.getRegAllocator().free(rCond);
        }

        thenBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(labelEnd));

        compiler.addLabel(labelElse);
        if (elseBranch ! null){
            elseBranch.codeGenListInst(compiler);
        }

        compiler.addLabel(labelEnd);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if (");
        condition.decompile(s);
        s.println(") {");
        s.indent();
        thenBranch.decompile(s);
        s.unindent();
        s.println("} else {");
        s.indent();
        elseBranch.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
