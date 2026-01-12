package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Gestionnaire de pile et de mémoire pour la génération de code.
 * 
 * Zones mémoire :
 * - GB : tables de méthodes + variables globales
 * - LB : variables locales d'une méthode ou d'un init
 */
public class StackManager {

    private int currentTemp = 0;
    private int maxTemp = 0;

    /**
     * Utilisation de n mots temporaires
     * @param n
     */
    public void useTemp(int n){
        if (n <= 0) return;
        currentTemp += n;
        if (currentTemp > maxTemp){
            maxTemp = currentTemp;
        }
    }

    /**
     * Libère n mots temporaires
     * @param n
     */
    public void releaseTemp(int n){
        if (n <= 0) return;
        currentTemp -= n;
        if (currentTemp < 0){
            currentTemp = 0;
        }
    }

    /** @return nombre maximal de temporaires utilisés dans le bloc*/
    public int getMaxTemp(){
        return maxTemp;
    }

    /**@return nombre courant de temporaires */
    public int getCurrentTemp(){
        return currentTemp;
    }

    /** Réinitialise l'usage des temporaires pour un nouveau bloc*/
    public void resetTemp(){
        currentTemp = 0;
        maxTemp = 0;
    }

    /**Allocation des variables*/
    private int nextGB = 1;
    private int nextLB = 1;

    /**
     * Alloue une variable globale (1 mot en GB)
     */
    public RegisterOffset allocGlobal() {
        RegisterOffset addr = new RegisterOffset(nextGB, Register.GB);
        nextGB++;
        return addr;
    }

    /**
     * Alloue un bloc continu en GB.
     * Utilisé pour réserver une table de méthodes.
     * @param size nombre de mots à réserver 
     * @return adresse de base du bloc réservé
     */
    public RegisterOffset allocGlobalBlock(int size){
        if (size <= 0){
            throw new IllegalArgumentException(
                "allocGlobalBlock : la taille doit être strictement positive."
            );
        }
        RegisterOffset base = new RegisterOffset(nextGB, Register.GB);
        nextGB += size;
        return base;
    }

    /**
     * Alloue une variable locale dans le bloc courant.
     * @return
     */
    public RegisterOffset allocLocal() {
        RegisterOffset addr = new RegisterOffset(nextLB, Register.LB);
        nextLB++;
        return addr;
    }

    /**
     * @return nombre total de mots globaux alloués.
    */
    public int getGlobalCount() {
        // offsets start at 1 => number of allocated globals = nextGB - 1
        return nextGB - 1;
    }

    /**
     * @return nombre de variables locales du bloc courant.
     */
    public int getLocalCount() {
        return nextLB - 1;
    }

    /**
     * Réinitialise les allocations globales et locales.
     * Appelé au debut de la génération du programme principal.
     */
    public void resetVars() {
        nextGB = 1;
        nextLB = 1;
    }

    /**
     * Contexte sauvegardé lors de l'entrée dans un bloc.
     */
    private static final class BlockContext {
        final int savedNextLB;
        final int savedCurrentTemp;
        final int savedMaxTemp;

        BlockContext(int savedNextLB, int savedCurrentTemp, int savedMaxTemp){
            this.savedNextLB = savedNextLB;
            this.savedCurrentTemp = savedCurrentTemp;
            this.savedMaxTemp = savedMaxTemp;
        }
    }

    /** Pile de contextes de blocs.*/
    private final Deque<BlockContext> blockStack = new ArrayDeque<>();

    /**
     * Entrée dans un nouveau bloc (méthode ou init).
     * - Réinitialise les variables locales
     * - Réinitialise le calcul des temporaires
     */
    public void enterBlock(){
        blockStack.push(new BlockContext(nextLB, currentTemp, maxTemp));
        nextLB = 1; // À vérifier : les locales commencent à 1(LB) pour chaque bloc
        resetTemp(); // Un TSTO par bloc
    }

    /**
     * Sortie du bloc courant.
     * Restaure le contexte précédent.
     */
    public void exitBlock(){
        if (blockStack.isEmpty()){
            throw new IllegalStateException(
                "exitBlock appelé sans enterBlock correspondant."
            );
        }
        BlockContext ctx = blockStack.pop();
        nextLB = ctx.savedNextLB;
        currentTemp = ctx.savedCurrentTemp;
        maxTemp = ctx.savedMaxTemp;
    }

    /**
     * Valeurs utilisés pour TSTO / ADDSP
     */

    /**
     * ADDSP du programme principal :
     * réserve uniquement les variables globales et les tables de méthodes
     */
    public int getADDSPForMain(){
        return getGlobalCount();
    }

    /**
     * TSTO du programme principal :
     * tests globales + temporaires maximaux.
     */
    public int getTSTOForMain(){
        return getGlobalCount() + getMaxTemp();
    }

    /**
     * ADDSP d'une méthode ou d'un init :
     * réserve uniquement les variables locales.
     */
    public int getADDSPForLocals(){
        return getLocalCount();
    }

    /**
     * TSTO d'une méthode ou d'un init :
     * teste locales + temporaires maximaux.
     * @return
     */
    public int getTSTOForLocals(){
        return getLocalCount() + getMaxTemp();
    }
}