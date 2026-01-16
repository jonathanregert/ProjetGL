package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.codegen.ErrorManager.RuntimeError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

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
    private boolean needRuntimeCheck = false;

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

    // @Override
    // public Type verifyExpr(DecacCompiler compiler,
    //                        EnvironmentExp localEnv,
    //                        ClassDefinition currentClass)
    //         throws ContextualError {


    //     // t1 casté, t2 cible
    //     Type t1 = expr.verifyExpr(compiler, localEnv, currentClass); // Type source
    //     Type t2 = typeIdent.verifyType(compiler);

    //     if (t1.isVoid()) {
    //         throw new ContextualError("Cast impossible : l'expression source est de type void.", getLocation());    
    //     }

    //     // Cas autorisés en SANS OBJET :
    //     // Conforme à la sémantique paragramhe 9
    //     // int <-> float uniquement
    //     if ((t1.isInt() || t1.isFloat()) && (t2.isInt() || t2.isFloat())) {
    //         setType(t2);
    //         return t2;
    //     }

    //     // Cas autorisés AVEC OBJET  :
    //     // if (exprType.isClassOrNull() && targetType.isClass()) {
    //     //     // 4.a : Cast de 'null' vers une Classe -> Toujours valide
    //     //     if (exprType.isNull()) {
    //     //         setType(targetType);
    //     //         return targetType;
    //     // }

    //    // Verif de cast_compatible(env, T1, T2) = assign_compatible(env, T1, T2) OU assign_compatible(env, T2, T1)
    //    boolean assignCompatible1 = false; // assign_compatible(env, T1, T2) => On peut mettre T2 dans T1
    //    boolean assignCompatible2 = false; // assign_compatible(env, T2, T1) => On peut mettre T1 dans T2

    //     if (t1.isFloat() && t2.isInt()) {
    //         assignCompatible1 = true; // on peut faire int = (float)
    //     }
    //     // subtype(env, T2, T1)
    //     else {
    //         if (t2.sameType(t1)) { // Réflexivité : T est sous-type de T
    //             assignCompatible1 = true;
    //         } else if (t2.isNull() && t1.isClass()) { // null est sous-type de toute classe
    //             assignCompatible1 = true;
    //         } else if (t2.isClass() && t1.isClass()) { // Sous-typage classes
    //             if (((ClassType) t2).isSubClassOf((ClassType) t1)) {
    //                 assignCompatible1 = true;
    //             }
    //         }
    //     }

    //     // verif de assign compatible(env, T2, T1)
    //     if (t2.isFloat() && t1.isInt()) {
    //         assignCompatible2 = true; // on peut faire float = (int)
    //     } else {
    //         if (t1.sameType(t2)) { // Réflexivité
    //             assignCompatible2 = true;
    //         } else if (t1.isNull() && t2.isClass()) { // null est sous-type de toute classe
    //             assignCompatible2 = true;
    //         } else if (t1.isClass() && t2.isClass()) { // Sous-typage classes
    //             if (((ClassType) t1).isSubClassOf((ClassType) t2)) {
    //                 assignCompatible2 = true;
    //             }
    //         }
    //     }

    //     if (assignCompatible1 || assignCompatible2) {
    //         setType(t2);
    //         return t2;
    //     }

    //     throw new ContextualError("Cast impossible de " + t1 + " vers " + t2 + ".", getLocation());

    // }

    @Override
    public Type verifyExpr(DecacCompiler compiler,
                        EnvironmentExp localEnv,
                        ClassDefinition currentClass)
            throws ContextualError {

        Type t1 = expr.verifyExpr(compiler, localEnv, currentClass); // source
        Type t2 = typeIdent.verifyType(compiler);                    // cible

        needRuntimeCheck = false;

        if (t1.isVoid()) {
            throw new ContextualError(
                "Cast impossible : l'expression source est de type void.",
                getLocation()
            );
        }

        // --- SANS OBJET (parag 9) : float -> int uniquement ---
        if (t2.isInt() && t1.isFloat()) {
            setType(t2);
            return t2;
        }

        // --- AVEC OBJET : (UneClasse)(v) ---
        if (t2.isClass()) {

            // null -> classe : toujours OK
            if (t1.isNull()) {
                setType(t2);
                return t2;
            }

            // source doit être classe
            if (!t1.isClass()) {
                throw new ContextualError(
                    "Cast invalide : " + t1 + " vers " + t2,
                    getLocation()
                );
            }

            ClassType src = (ClassType) t1;
            ClassType dst = (ClassType) t2;

            // Cast trivial : src <= dst
            if (src.isSubClassOf(dst)) {
                needRuntimeCheck = false;
                setType(t2);
                return t2;
            }

            // Downcast possible : dst <= src => runtime check requis
            if (dst.isSubClassOf(src)) {
                needRuntimeCheck = true;
                setType(t2);
                return t2;
            }

            throw new ContextualError(
                "Cast invalide : classes incompatibles (" + t1 + " vers " + t2 + ")",
                getLocation()
            );
        }

        // Tout le reste interdit
        throw new ContextualError(
            "Cast impossible de " + t1 + " vers " + t2 + ".",
            getLocation()
        );
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        // 1) Résultat de expr dans target (souvent R1)
        expr.codeGenExpr(compiler, target);

        // 2) Cast primitif autorisé : float -> int
        if (getType().isInt() && expr.getType().isFloat()) {
            compiler.addInstruction(new INT(target, target));
            return;
        }

        // 3) Cast objet : vérif dynamique si nécessaire
        if (getType().isClass() && needRuntimeCheck && !compiler.getNoCheckOption()) {

            int id = compiler.getLabelId();
            Label ok = new Label("cast_ok_" + id);
            Label loop = new Label("cast_loop_" + id);
            Label fail = compiler.getErrorManager().label(RuntimeError.INVALID_CAST);

            // if (target == null) => ok
            compiler.addInstruction(new CMP(new NullOperand(), target));
            compiler.addInstruction(new BEQ(ok));

            // R0 = vtable dynamique = obj[0]
            compiler.addInstruction(new LOAD(new RegisterOffset(0, target), Register.R0));

            // On veut un registre pour la vtable cible SANS casser target.
            GPRegister tmp = compiler.getRegAllocator().alloc();
            boolean tmpFromAllocator = (tmp != null);
            boolean pushedTarget = false;

            // Si pas de registre dispo, on tente R1
            if (tmp == null) {
                tmp = Register.R1;
            }

            // Si tmp risque d'écraser target (tmp == target), on sauvegarde target
            if (tmp == target) {
                compiler.addInstruction(new PUSH(target));
                compiler.getStackManager().useTemp(1);
                pushedTarget = true;
            }

            // tmp = adresse vtable cible
            ClassType dst = (ClassType) getType();
            compiler.addInstruction(new LEA(dst.getDefinition().getAddrTable(), tmp));

            // boucle
            compiler.addLabelToBlock(loop);

            // if (R0 == tmp) ok
            compiler.addInstruction(new CMP(tmp, Register.R0));
            compiler.addInstruction(new BEQ(ok));

            // if (R0 == 0) fail
            compiler.addInstruction(new CMP(new NullOperand(), Register.R0));
            compiler.addInstruction(new BEQ(fail));

            // R0 = superVtable = vtable[0]
            compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R0), Register.R0));
            compiler.addInstruction(new BRA(loop));

            compiler.addLabelToBlock(ok);

            // restore target si on l'a push
            if (pushedTarget) {
                compiler.addInstruction(new POP(target));
                compiler.getStackManager().releaseTemp(1);
            }

            // free tmp si on l'a obtenu via l'allocateur
            if (tmpFromAllocator) {
                compiler.getRegAllocator().free(tmp);
            }
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

    @Override
    public int getPriorite() { return 90; }

}