package fr.ensimag.deca.codegen;

import fr.ensimag.deca.context.Type;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager pour le bytecode.
 *
 *
 * @author gl42
 */
public class ByteManager {

    private final List<String> instructions = new ArrayList<>();

    // Gestion de la pile

    public void emitLDC(int value) {
        instructions.add("ldc " + value);
    }

    public void emitLDC(float value) {
        instructions.add("ldc " + value);
    }

    public void emitLDC(String value) {
        instructions.add("ldc \"" + value + "\"");
    }

    // Arithmétique

    public void emitIADD() {
        instructions.add("iadd");
    }

    public void emitISUB() {
        instructions.add("isub");
    }

    public void emitIMUL() {
        instructions.add("imul");
    }

    public void emitIDIV() {
        instructions.add("idiv");
    }

    public void emitFDIV() {
        instructions.add("fdiv");
    }

    // Comparaisons / booléens

    public void emitICMP_EQ() {
        instructions.add("if_icmpeq");
    }

    public void emitICMP_NE() {
        instructions.add("if_icmpne");
    }

    public void emitICMP_LT() {
        instructions.add("if_icmplt");
    }

    public void emitICMP_LE() {
        instructions.add("if_icmple");
    }

    public void emitICMP_GT() {
        instructions.add("if_icmpgt");
    }

    public void emitICMP_GE() {
        instructions.add("if_icmpge");
    }

    // Entrées / sorties

    public void emitPrint(Type type) {
        if (type.isInt() || type.isBoolean()) {
            instructions.add(
                "invokestatic java/io/PrintStream.println(I)V"
            );
        } else if (type.isFloat()) {
            instructions.add(
                "invokestatic java/io/PrintStream.println(F)V"
            );
        } else {
            throw new UnsupportedOperationException(
                "print JVM non supporté pour " + type
            );
        }
    }

    public void emitNewline() {
        instructions.add("println");
    }

    public void emitReadInt() {
        instructions.add(
            "invokestatic java/util/Scanner.nextInt()I"
        );
    }

    // Contrôle

    public void emitReturn() {
        instructions.add("return");
    }

    // Debug / sortie

    public void dump() {
        System.out.println("----------------- BYTECODE (DEBUG) -----------------");
        for (String ins : instructions) {
            System.out.println(ins);
        }
    }

    public void dumpToFile(File file) throws FileNotFoundException {
        try (PrintStream ps = new PrintStream(file)) {
            for (String ins : instructions) {
                ps.println(ins);
            }
        }
    }
    
    public List<String> getInstructions() {
        return instructions;
    }

    // sauts
    public void emitIfEq(String label) {
        instructions.add("ifeq " + label);
    }

    public void emitIfNe(String label) {
        instructions.add("ifne " + label);
    }

    // conversions
    public void emitI2F() {
        instructions.add("i2f");
    }

    public void emitF2I() {
        instructions.add("f2i");
    }

    public void emitIREM() {
        instructions.add("irem");
    }

    private int labelCounter = 0;

    public String newLabel() {
        return "L" + (labelCounter++);
    }

    public void emitGoto(String label) {
        instructions.add("goto " + label);
    }

    public void emitLabel(String label) {
        instructions.add(label + ":");
    }

    public void emitCmp(String op) {
        instructions.add("cmp_" + op);
    }

    public void emitIfCmp(String op, String label) {
        instructions.add("if_icmp" + op + " " + label);
    }

    // Unaires

    public void emitINEG() {
        instructions.add("ineg");
    }

    // ReadFloat + return typé

    public void emitReadFloat() {
        instructions.add("invokestatic java/util/Scanner.nextFloat()F");
    }
    public void emitReturnVoid() {
        instructions.add("return");
    }
    public void emitIReturn() {
        instructions.add("ireturn");
    }
    public void emitFReturn() {
        instructions.add("freturn");
    }

}
