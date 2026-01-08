package fr.ensimag.deca.codegen;

import java.util.EnumMap;
import java.util.EnumSet;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.DecacFatalError;
import fr.ensimag.ima.pseudocode.GPRegister;
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
        DIV_BY_ZERO,
        ARITH_OVERFLOW,
        NULL_DEREF,
        CAST_FAIL
    }

    private final EnumSet<RuntimeError> used = EnumSet.noneOf(RuntimeError.class);
    private final EnumMap<RuntimeError, Label> labels = new EnumMap<>(RuntimeError.class);

    public Label label(RuntimeError error){
        used.add(error);
        return labels.computeIfAbsent(error, e -> new Label(labelName(e)));
    }

    private String labelName(RuntimeError error){
        switch(error){
            case STACK_OVERFLOW : return "pile_pleine";
            case DIV_BY_ZERO :    return "division_par_zero";
            case ARITH_OVERFLOW : return "overflow_arith";
            case NULL_DEREF:      return "deference_null";
            case CAST_FAIL :      return "cast_invalide";
            default:              return "runtime_error";
        }
    }

    private String message(RuntimeError error){
        switch (error){
            case STACK_OVERFLOW: return "Erreur : débordement de pile";
            case DIV_BY_ZERO:    return "Erreur ; division par zero";
            case ARITH_OVERFLOW: return "Erreur : overflow_arith";
            case NULL_DEREF:     return "deference_null";
            case CAST_FAIL:      return "cast_invalide";
            default:             return "runtime_error";
        }
    }

    /*Pour les checks*/
    public void genCheckDivByZero(DecacCompiler compiler, GPRegister divisor){
        compiler.addInstruction(new CMP(new ImmediateInteger(0), divisor));
        compiler.addInstruction(new BEQ(label(RuntimeError.DIV_BY_ZERO)));
    }

    public void genCheckOverflow(DecacCompiler compiler){
        compiler.addInstruction(new BOV(label(RuntimeError.ARITH_OVERFLOW)));
    }
    
    public void genCheckNullDeref(DecacCompiler compiler, GPRegister objReg){
        compiler.addInstruction(new CMP(new ImmediateInteger(0), objReg));
        compiler.addInstruction(new BEQ(label(RuntimeError.NULL_DEREF)));
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