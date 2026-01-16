package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
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


        // t1 casté, t2 cible
        Type t1 = expr.verifyExpr(compiler, localEnv, currentClass); // Type source
        Type t2 = typeIdent.verifyType(compiler);

        if (t1.isVoid()) {
            throw new ContextualError("Cast impossible : l'expression source est de type void.", getLocation());    
        }

        // Cas autorisés en SANS OBJET :
        // Conforme à la sémantique paragramhe 9
        // int <-> float uniquement
        if ((t1.isInt() || t1.isFloat()) && (t2.isInt() || t2.isFloat())) {
            setType(t2);
            return t2;
        }

        // Cas autorisés AVEC OBJET  :
        // if (exprType.isClassOrNull() && targetType.isClass()) {
        //     // 4.a : Cast de 'null' vers une Classe -> Toujours valide
        //     if (exprType.isNull()) {
        //         setType(targetType);
        //         return targetType;
        // }

       // Verif de cast_compatible(env, T1, T2) = assign_compatible(env, T1, T2) OU assign_compatible(env, T2, T1)
       boolean assignCompatible1 = false; // assign_compatible(env, T1, T2) => On peut mettre T2 dans T1
       boolean assignCompatible2 = false; // assign_compatible(env, T2, T1) => On peut mettre T1 dans T2

       if (t1.isFloat() && t2.isInt()) {
            assignCompatible1 = true; // on peut faire int = (float)
        }
        // subtype(env, T2, T1)
        else {
            if (t2.sameType(t1)) { // Réflexivité : T est sous-type de T
                assignCompatible1 = true;
            } else if (t2.isNull() && t1.isClass()) { // null est sous-type de toute classe
                assignCompatible1 = true;
            } else if (t2.isClass() && t1.isClass()) { // Sous-typage classes
                if (((ClassType) t2).isSubClassOf((ClassType) t1)) {
                    assignCompatible1 = true;
                }
            }
        }

        // verif de assign compatible(env, T2, T1)
        if (t2.isFloat() && t1.isInt()) {
            assignCompatible2 = true; // on peut faire float = (int)
        } else {
            if (t1.sameType(t2)) { // Réflexivité
                assignCompatible2 = true;
            } else if (t1.isNull() && t2.isClass()) { // null est sous-type de toute classe
                assignCompatible2 = true;
            } else if (t1.isClass() && t2.isClass()) { // Sous-typage classes
                if (((ClassType) t1).isSubClassOf((ClassType) t2)) {
                    assignCompatible2 = true;
                }
            }
        }

        if (assignCompatible1 || assignCompatible2) {
            setType(t2);
            return t2;
        }

        throw new ContextualError("Cast impossible de " + t1 + " vers " + t2 + ".", getLocation());

    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        expr.codeGenExpr(compiler, target);
        // if (getType().isFloat() && expr.getType().isInt()) {
        //     compiler.addInstruction(new FLOAT(target, target));
        // }
        if (getType().isInt() && expr.getType().isFloat()) {
            compiler.addInstruction(new INT(target, target));
        }
        //avecObjet
        
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

    @Override
    public int getPriorite() { return 90; }

}