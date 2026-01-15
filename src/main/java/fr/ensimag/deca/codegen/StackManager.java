package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Gestionnaire de pile et de mémoire pour la génération de code.
 *
 * Zones mémoire :
 * - GB : tables de méthodes + variables globales (main)
 * - LB : variables locales d'une méthode ou d'un init (frame)
 *
 * Convention :
 * - enterBlock/exitBlock sont utilisés uniquement pour les frames (méthodes/inits).
 * - Le main ne doit pas appeler enterBlock : ses variables restent en GB.
 */
public class StackManager {

    // --- Temporaires ---
    private int currentTemp = 0;
    private int maxTemp = 0;

    public void useTemp(int n) {
        if (n <= 0) return;
        currentTemp += n;
        if (currentTemp > maxTemp) maxTemp = currentTemp;
    }

    public void releaseTemp(int n) {
        if (n <= 0) return;
        currentTemp -= n;
        if (currentTemp < 0) currentTemp = 0;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public void resetTemp() {
        currentTemp = 0;
        maxTemp = 0;
    }

    // --- Allocation variables ---
    private int nextGB = 1;
    private int nextLB = 1;

    public RegisterOffset allocGlobal() {
        RegisterOffset addr = new RegisterOffset(nextGB, Register.GB);
        nextGB++;
        return addr;
    }

    public RegisterOffset allocGlobalBlock(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("allocGlobalBlock : la taille doit être strictement positive.");
        }
        RegisterOffset base = new RegisterOffset(nextGB, Register.GB);
        nextGB += size;
        return base;
    }

    public RegisterOffset allocLocal() {
        RegisterOffset addr = new RegisterOffset(nextLB, Register.LB);
        nextLB++;
        return addr;
    }

    /** Alloue une variable selon le contexte : LB si dans une frame, sinon GB. */
    public RegisterOffset allocVar() {
        return inBlock() ? allocLocal() : allocGlobal();
    }

    public int getGlobalCount() {
        return nextGB - 1;
    }

    public int getLocalCount() {
        return nextLB - 1;
    }

    public void resetVars() {
        nextGB = 1;
        nextLB = 1;
    }

    // --- Gestion des frames (méthodes / init) ---
    private static final class BlockContext {
        final int savedNextLB;
        final int savedCurrentTemp;
        final int savedMaxTemp;

        BlockContext(int savedNextLB, int savedCurrentTemp, int savedMaxTemp) {
            this.savedNextLB = savedNextLB;
            this.savedCurrentTemp = savedCurrentTemp;
            this.savedMaxTemp = savedMaxTemp;
        }
    }

    private final Deque<BlockContext> blockStack = new ArrayDeque<>();

    /** true si on est dans une frame (méthode/init). */
    public boolean inBlock() {
        return !blockStack.isEmpty();
    }

    /** Entrée dans une frame (méthode/init). */
    public void enterBlock() {
        blockStack.push(new BlockContext(nextLB, currentTemp, maxTemp));
        nextLB = 1;     // locales à partir de 1(LB)
        resetTemp();    // temporaires comptés par frame
    }

    /** Sortie de la frame courante. */
    public void exitBlock() {
        if (blockStack.isEmpty()) {
            throw new IllegalStateException("exitBlock appelé sans enterBlock correspondant.");
        }
        BlockContext ctx = blockStack.pop();
        nextLB = ctx.savedNextLB;
        currentTemp = ctx.savedCurrentTemp;
        maxTemp = ctx.savedMaxTemp;
    }

    // --- Valeurs pour prologues ---
    public int getADDSPForMain() {
        return getGlobalCount();
    }

    // À revoir le + 2
    public int getTSTOForMain() {
        return getGlobalCount() + getMaxTemp() + 2;
    }

    public int getADDSPForLocals() {
        return getLocalCount();
    }

    public int getTSTOForLocals() {
        return getLocalCount() + getMaxTemp();
    }
}
