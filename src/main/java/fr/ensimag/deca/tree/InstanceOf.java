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
        // Vérification de l'expression : Type
        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);
        // cible doit être une classe
        Type cibleType = typeName.verifyType(compiler);

        if (!exprType.isClass() && !exprType.isNull()) {
            throw new ContextualError("L'opérateur instanceof s'applique à un objet, pas à " + exprType, expr.getLocation());
        }
        if (!cibleType.isClass()) {
            throw new ContextualError("Le deuxième argument d'instanceof doit être une classe", typeName.getLocation());
        }

        Type boolType = compiler.environmentType.BOOLEAN;
        this.setType(boolType);
        return boolType;
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        throw new UnsupportedOperationException("Not yet implemented");
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