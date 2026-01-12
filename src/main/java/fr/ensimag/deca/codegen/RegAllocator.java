package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;

public class RegAllocator {
    private final int maxReg;
    private final boolean[] used;

    private static final int FIRST_ALLOC = 3;

    public RegAllocator(int maxReg){
        if (maxReg < FIRST_ALLOC){
            throw new IllegalArgumentException("maxReg must be >= "  + FIRST_ALLOC + " need R1 target, R2 spill, and at least one temp)");
        }
        this.maxReg = maxReg;
        this.used = new boolean[maxReg + 1];
    }

    public int getFirstAlloc() {
        return FIRST_ALLOC;
    }

    /**
     * Allocate a free register among R3..Rmax
     * @return a GPRegister if available, otherwise null
     */

    public GPRegister alloc(){
        for (int i = FIRST_ALLOC; i <= maxReg; i++){
            if (!used[i]){
                used[i] = true;
                return Register.getR(i);
            }
        }
        return null;
    }

    /**
     * free a previously allocated register (must be in R3...Rmax).
     */
    public void free(GPRegister r){
        if (r==null) return;

        int i = r.getNumber();

        if (i < FIRST_ALLOC) return;

        if(i < 0 || i > maxReg){
            throw new IllegalArgumentException("Trying to free a out-of-range register: R" + i);
        }
        if (!used[i]){
            throw new IllegalStateException("Double free or freeing a non-allocated register: R" + i);
        }
        used[i] = false;
    }

    /**
    * Mark all registers as free
    */
    public void reset(){
        for (int i = FIRST_ALLOC; i <= maxReg; i++){
            used[i] = false;
        }
    }

    /**
     * @return highest allocatable register number (R1..Rmax).
     */
    public int getMaxReg(){
        return maxReg;
    }

    /**
     * Debug helper : check if a register is currently allocated.
     */
    public boolean isUsed(GPRegister r){
        if (r == null) return false;
        int i = r.getNumber();
        if (i <= FIRST_ALLOC || i > maxReg) return false;
        return used[i];
    }

    public boolean[] getUsedSnaphot(){
        return used.clone();
    }
}
