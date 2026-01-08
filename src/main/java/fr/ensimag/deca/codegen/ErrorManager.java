package fr.ensimag.deca.codegen;

import java.util.EnumMap;
import java.util.EnumSet;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.DecacFatalError;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

public class ErrorManager {

    public enum RuntimeError{
        STACK_OVERFLOW,
        HEAP_OVERFLOW,

        //Arithmétique
        INT_DIV_BY_ZERO,
        INT_MOD_BY_ZERO,
        FLOAT_DIV_BY_ZERO,
        ARITH_OVERFLOW,

        // entrée/sortie
        READ_INT_ERROR,
        READ_FLOAT_ERROR
    }

    private final EnumSet<RuntimeError> used = EnumSet.noneOf(RuntimeError.class);
    private final EnumMap<RuntimeError, Label> labels = new EnumMap<>(RuntimeError.class);

    public Label label(RuntimeError error){
        used.add(error);
        return labels.computeIfAbsent(error, e -> new Label(labelName(e)));
    }

    private String labelName(RuntimeError error){
        switch(error){
            case STACK_OVERFLOW :    return "pile_pleine";
            case HEAP_OVERFLOW :     return "tas_plein";

            case INT_DIV_BY_ZERO:    return "division par zero";
            case INT_MOD_BY_ZERO:    return "reste_par_zero";
            case FLOAT_DIV_BY_ZERO: return "division_par_zero_float";

            case ARITH_OVERFLOW :    return "overflow_arith";

            case READ_INT_ERROR:     return "erreur_lecture_int";
            case READ_FLOAT_ERROR:   return "erreur_lecture_float";

            default:                 return "runtime_error";
        }
    }

    private String message(RuntimeError error){
        switch (error){
            case STACK_OVERFLOW:    return "Erreur : debordement de pile";
            case HEAP_OVERFLOW:     return "Erreur : debordement de tas";

            case INT_DIV_BY_ZERO:   return "Erreur : divion entiere par zero";
            case INT_MOD_BY_ZERO:   return "Erreur : reste de la division entiere par zero";
            case FLOAT_DIV_BY_ZERO: return "Erreur : division par 0.0";

            case ARITH_OVERFLOW:    return "Erreur : overflow_arithmetique";

            case READ_INT_ERROR:    return "Erreur : lecture d'un entier invalide";
            case READ_FLOAT_ERROR:  return "Erreur : lecture d'un flottant invalide";
            default:                return "runtime_error";
        }
    }

    public void genCheckStackOverflow(DecacCompiler compiler) {
        compiler.addInstruction(new BOV(label(RuntimeError.STACK_OVERFLOW)));
    }

    public void genCheckHeapOverflow(DecacCompiler compiler) {
        compiler.addInstruction(new BOV(label(RuntimeError.HEAP_OVERFLOW)));
    }

    public void genCheckIntDivByZero(DecacCompiler compiler, GPRegister divisor) {
        compiler.addInstruction(new CMP(new ImmediateInteger(0), divisor));
        compiler.addInstruction(new BEQ(label(RuntimeError.INT_DIV_BY_ZERO)));
    }

    public void genCheckFloatDivByZero(DecacCompiler compiler, GPRegister divisor) {
        compiler.addInstruction(new CMP(new ImmediateFloat(0), divisor));
        compiler.addInstruction(new BEQ(label(RuntimeError.FLOAT_DIV_BY_ZERO)));
    }

    public void genCheckIntModByZero(DecacCompiler compiler, GPRegister divisor) {
        compiler.addInstruction(new CMP(new ImmediateInteger(0), divisor));
        compiler.addInstruction(new BEQ(label(RuntimeError.INT_MOD_BY_ZERO)));
    }

    /** Overflow arithmétique (si vous décidez de le gérer maintenant). */
    public void genCheckOverflow(DecacCompiler compiler) {
        compiler.addInstruction(new BOV(label(RuntimeError.ARITH_OVERFLOW)));
    }

    /** Après RINT */
    public void genCheckReadInt(DecacCompiler compiler) {
        compiler.addInstruction(new BOV(label(RuntimeError.READ_INT_ERROR)));
    }

    /** Après RFLOAT */
    public void genCheckReadFloat(DecacCompiler compiler) {
        compiler.addInstruction(new BOV(label(RuntimeError.READ_FLOAT_ERROR)));
    }

    public void emitHandlers(DecacCompiler compiler){
        if (used.isEmpty()){
            return;
        }

        compiler.addComment("Message d'erreurs");
        for (RuntimeError error : RuntimeError.values()){
            if (!used.contains(error)){
                continue;
            }
            Label l = labels.get(error);
            compiler.addLabel(l);
            compiler.addInstruction(new WSTR(message(error)));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
        }
    }
}