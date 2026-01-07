package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.GPRegister;

public class StackManager {
    private int currentTemp = 0;
    private int maxTemp = 0;

    public void useTemp(int n){
        if (n <= 0) return;
        currentTemp += n;
        if (currentTemp > maxTemp){
            maxTemp = currentTemp;
        }
    }

    public void releaseTemp(int n){
        if (n <= 0) return;
        currentTemp -= n;
        if (currentTemp < 0){
            currentTemp = 0;
        }
    }

    public int getMaxTemp(){
        return maxTemp;
    }

    public int getCurrentTemp(){
        return currentTemp;
    }

    public void resetTemp(){
        currentTemp = 0;
        maxTemp = 0;
    }

    private int nextGB = 1;
    private int nextLB = 1;

    public RegisterOffset allocGlobal() {
        RegisterOffset addr = new RegisterOffset(nextGB, Register.GB);
        nextGB++;
        return addr;
    }

    public RegisterOffset allocLocal() {
        RegisterOffset addr = new RegisterOffset(nextLB, Register.LB);
        nextLB++;
        return addr;
    }

    public int getGlobalCount() {
        // offsets start at 1 => number of allocated globals = nextGB - 1
        return nextGB - 1;
    }

    public int getLocalCount() {
        return nextLB - 1;
    }

    public void resetVars() {
        nextGB = 1;
        nextLB = 1;
    }

    public int getTSTOForMain(){
        return getGlobalCount() + getMaxTemp();
    }

    public int getTSTOForLoacals(){
        return getLocalCount() + getMaxTemp();
    }
}
