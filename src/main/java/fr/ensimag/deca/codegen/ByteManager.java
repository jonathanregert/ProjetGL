package fr.ensimag.deca.codegen;

import fr.ensimag.deca.context.Type;

import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manager pour le bytecode.
 *
 *
 * @author gl42
 */
public class ByteManager {
    private int currentStack = 0;
    private int maxStack = 0;

    private final Map<String, List<String>> classInstructions = new HashMap<>();
    @SuppressWarnings("unused")
    private String currentClassName = "Main";
    private List<String> instructions = new ArrayList<>();

    private void updateStack(int delta) {
        currentStack += delta;
        maxStack = Math.max(maxStack, currentStack);
    }

    public int getMaxStack() {
        return maxStack;
    }

    public void enterMethod() {
        currentStack = 0;
        maxStack = 0;
    }

    public void startClass(String className, String superInternalName) {
        currentClassName = className;
        instructions = classInstructions.computeIfAbsent(className, k -> new ArrayList<>());
        instructions.clear();

        instructions.add(".class public " + className);
        instructions.add(".super " + superInternalName);
        instructions.add("");
    }

    public List<String> getInstructionsForClass(String className) {
        return classInstructions.get(className);
    }

    // Gestion de la pile

    public void emitLDC(int value) {
        instructions.add("ldc " + value);
        updateStack(+1);
    }

    public void emitLDC(float value) {
        instructions.add("ldc " + value);
        updateStack(+1);
    }

    public void emitLDC(String value) {
        instructions.add("ldc \"" + value + "\"");
        updateStack(+1);
    }

    // Arithmétique

    public void emitIADD() {
        instructions.add("iadd");
        updateStack(-1);
    }

    public void emitISUB() {
        instructions.add("isub");
        updateStack(-1);
    }

    public void emitIMUL() {
        instructions.add("imul");
        updateStack(-1);
    }

    public void emitIDIV() {
        instructions.add("idiv");
        updateStack(-1);
    }

    public void emitFDIV() {
        instructions.add("fdiv");
        updateStack(-1);
    }

    public void emitFADD() 
    { 
        instructions.add("fadd"); 
        updateStack(-1);
    }
    public void emitFSUB() 
    { 
        instructions.add("fsub");
        updateStack(-1);
    }
    public void emitFMUL() 
    { 
        instructions.add("fmul");
        updateStack(-1);
    }
    public void emitFNEG() { instructions.add("fneg"); }


    // Comparaisons / booléens

    public void emitICMP_EQ() {
        instructions.add("if_icmpeq");
    }

    public void emitICMP_NE() {
        instructions.add("if_icmpne");
    }


    public void emitIfACmpEq(String label) {
        instructions.add("if_acmpeq " + label);
        updateStack(-2); // Consomme 2 références
    }


    public void emitIfACmpNe(String label) {
        instructions.add("if_acmpne " + label);
        updateStack(-2); // Consomme 2 références
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

    public void emitIfCmpFloatTrue(String op, String label) {
        switch (op) {
            case "<":  instructions.add("iflt " + label); break;
            case "<=": instructions.add("ifle " + label); break;
            case ">":  instructions.add("ifgt " + label); break;
            case ">=": instructions.add("ifge " + label); break;
            case "==": instructions.add("ifeq " + label); break;
            case "!=": instructions.add("ifne " + label); break;
            default:
                throw new IllegalArgumentException("op float inconnu: " + op);
        }
        updateStack(-1);
    }


    public void emitFCMPL() {
        instructions.add("fcmpl");
        updateStack(-1);
    }



    // Entrées / sorties

    public void emitPrint(Type type) {
        instructions.add("getstatic java/lang/System/out Ljava/io/PrintStream;");
        updateStack(+1); // out pousse 1 ref

        instructions.add("swap"); // ne change pas la hauteur

        if (type.isInt() || type.isBoolean()) {
            instructions.add("invokevirtual java/io/PrintStream/print(I)V");
        } else if (type.isFloat()) {
            instructions.add("invokevirtual java/io/PrintStream/print(F)V");
        } else if (type.isString()) {
            instructions.add("invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V");
        } else {
            throw new UnsupportedOperationException("print JVM non supporté pour " + type);
        }

        updateStack(-2); // consomme (out, value), retourne void
    }

    public void emitPrintln(Type type) {
        instructions.add("getstatic java/lang/System/out Ljava/io/PrintStream;");
        updateStack(+1);

        instructions.add("swap");

        if (type.isInt() || type.isBoolean()) {
            instructions.add("invokevirtual java/io/PrintStream/println(I)V");
        } else if (type.isFloat()) {
            instructions.add("invokevirtual java/io/PrintStream/println(F)V");
        } else if (type.isString()) {
            instructions.add("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
        } else {
            throw new UnsupportedOperationException("println JVM non supporté pour " + type);
        }

        updateStack(-2);
    }



    public void emitNewline() {
        instructions.add("getstatic java/lang/System/out Ljava/io/PrintStream;");
        updateStack(+1);

        instructions.add("invokevirtual java/io/PrintStream/println()V");
        updateStack(-1);
    }



    public void emitReadInt() {
        instructions.add(
            "invokestatic java/util/Scanner.nextInt()I"
        );
        updateStack(+1);
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

    // ecriture finale des inst jasmin
    public void dumpClassToFile(String className, File file) throws FileNotFoundException {
        List<String> ins = classInstructions.get(className);
        if (ins == null) {
            throw new IllegalArgumentException("No bytecode emitted for class " + className);
        }
        try (PrintStream ps = new PrintStream(file)) {
            for (String line : ins) {
                ps.println(line);
            }
        }
    }
    //multi fichier
    public void dumpAllToDirectory(File dir) throws FileNotFoundException {
        for (Map.Entry<String, List<String>> e : classInstructions.entrySet()) {
            File out = new File(dir, e.getKey() + ".j");
            try (PrintStream ps = new PrintStream(out)) {
                for (String ins : e.getValue()) {
                    ps.println(ins);
                }
            }
        }
    }
    
    public List<String> getInstructions() {
        return instructions;
    }

    // sauts
    public void emitIfEq(String label) {
        instructions.add("ifeq " + label);
        updateStack(-1);
    }

    public void emitIfNe(String label) {
        instructions.add("ifne " + label);
        updateStack(-1);
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
        updateStack(-2);
    }

    // Unaires

    public void emitINEG() {
        instructions.add("ineg");
    }

    // ReadFloat + return typé

    public void emitReadFloat() {
        instructions.add("invokestatic java/util/Scanner.nextFloat()F");
        updateStack(+1);
    }
    public void emitReturnVoid() {
        instructions.add("return");
        currentStack = 0;
    }
    public void emitIReturn() {
        instructions.add("ireturn");
        currentStack = 0;
    }
    public void emitFReturn() {
        instructions.add("freturn");
        currentStack = 0;
    }

    // locals
    public void emitILoad(int slot) {
        instructions.add("iload " + slot);
        updateStack(+1);
    }

    public void emitIStore(int slot) {
        instructions.add("istore " + slot);
        updateStack(-1);
    }

    public void emitFLoad(int slot) {
        instructions.add("fload " + slot);
        updateStack(+1);
    }

    public void emitFStore(int slot) {
        instructions.add("fstore " + slot);
        updateStack(-1);
    }

    // stack
    public void emitDUP() {
        instructions.add("dup");
        updateStack(+1);
    }

    public void emitDUP_X1() {
        instructions.add("dup_x1");
        updateStack(+1);
    }

    // references
    public void emitALoad(int slot) {
        instructions.add("aload " + slot);
        updateStack(+1);
    }

    public void emitAStore(int slot) {
        instructions.add("astore " + slot);
        updateStack(-1);
    }

    public void emitAConstNull() {
        instructions.add("aconst_null");
        updateStack(+1);
    }

    public void emitAReturn() {
        instructions.add("areturn");
        currentStack = 0;
    }

    public void emitNew(String internalClassName) {
        instructions.add("new " + internalClassName);
        updateStack(+1);
    }

    public void emitInvokeSpecial(String ownerInternal, String name, String desc) {
        instructions.add("invokespecial " + ownerInternal + "/" + name + desc);
    }

    public void emitInvokeVirtual(String ownerInternal, String name, String desc) {
        instructions.add("invokevirtual " + ownerInternal + "/" + name + desc);
    }

    public void emitGetField(String ownerInternal, String fieldName, String desc) {
        instructions.add("getfield " + ownerInternal + "/" + fieldName + " " + desc);
    }

    public void emitPutField(String ownerInternal, String fieldName, String desc) {
        instructions.add("putfield " + ownerInternal + "/" + fieldName + " " + desc);
    }

    public static String typeToDescriptor(Type type) {
        if (type.isInt()) return "I";
        if (type.isFloat()) return "F";
        if (type.isBoolean()) return "Z";
        if (type.isVoid()) return "V";
        if (type.isString()) return "Ljava/lang/String;";
        if (type.isClass()) return "L" + toInternalClassName(type.getName().getName()) + ";";
        throw new UnsupportedOperationException("Type JVM non supporté: " + type);
    }


    public static String toInternalClassName(String name) {
        if ("Object".equals(name)) {
            return "java/lang/Object";
        }
        return name;
    }

    public void emitInstanceOf(String className) {
        instructions.add("instanceof " + className);
    }

    public void emitCheckCast(String className) {
        instructions.add("checkcast " + className);
    }

}
