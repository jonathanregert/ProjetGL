package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Explicit cast expression: (type)(expr)
 *
 * @author gl42
 */
public class Cast extends AbstractExpr {

    private final AbstractIdentifier typeIdent;
    private final AbstractExpr expr;

    public Cast(AbstractIdentifier typeIdent, AbstractExpr expr) {
        Validate.notNull(typeIdent);
        Validate.notNull(expr);
        this.typeIdent = typeIdent;
        this.expr = expr;
    }

    public AbstractIdentifier getTypeIdent() {
        return typeIdent;
    }

    public AbstractExpr getExpr() {
        return expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler,
                           EnvironmentExp localEnv,
                           ClassDefinition currentClass)
            throws ContextualError {

        // Type de l'expression castée
        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);

        // Type cible
        Type targetType = typeIdent.verifyType(compiler);

        // Cas autorisés en SANS OBJET :
        // Conforme à la sémantique paragramhe 9
        // int <-> float uniquement
        if (targetType.isFloat() && exprType.isInt()) {
            setType(targetType);
            return targetType;
        }

        if (targetType.isInt() && exprType.isFloat()) {
            setType(targetType);
            return targetType;
        }

        // Tous les autres casts sont interdits
        throw new ContextualError(
            "Cast invalide de " + exprType + " vers " + targetType,
            getLocation()
        );
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        expr.codeGenExpr(compiler, target);

        // Conversion selon les types
        if (getType().isFloat() && expr.getType().isInt()) {
            compiler.addInstruction(new FLOAT(target, target));
        }
        else if (getType().isInt() && expr.getType().isFloat()) {
            compiler.addInstruction(new INT(target, target));
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        typeIdent.decompile(s);
        s.print(")(");
        expr.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        typeIdent.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        typeIdent.iter(f);
        expr.iter(f);
    }
}