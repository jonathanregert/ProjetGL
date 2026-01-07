package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;

public class RegAllocator {
    private final int maxReg;
    private final boolean[] used;

    public RegAllocator(int maxReg){
        if (maxReg < 1){
            throw new IllegalArgumentException("maxReg must be >=.");
        }
        this.maxReg = maxReg;
        this.used = new boolean[maxReg + 1];
    }

    /**
     * Allocate a free register among R1..Rmax
     * @return a GPRegister if available, otherwise null
     */

    public GPRegister alloc(){
        for (int i = 1; i <= maxReg; i++){
            if (!used[i]){
                used[i] = true;
                return Register.getR(i);
            }
        }
        return null;
    }

    /**
     * free a previously allocated register
     */
    public void free(GPRegister r){
        if (r==null) return;

        int i = r.getNumber();

        if (i==0) return;

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
        for (int i = 1; i < maxReg; i++){
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
        if (i <= 0 || i > maxReg) return false;
        return used[i];
    }
    
}
