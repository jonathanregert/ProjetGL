package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * @author gl42
 * @date 01/01/2026
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        
        Type t = type.verifyType(compiler);
        if (t.isVoid()) {
            // Message d'erreur si le type est void + ligne : La variable "[name]" ne peut pas être de type "void"
            throw new ContextualError("La variable \"" + varName.getName().getName() + "\" ne peut pas être de type \"void\"", varName.getLocation());
        }
        
        initialization.verifyInitialization(compiler, t, localEnv, currentClass);

        VariableDefinition varDef = new VariableDefinition(t, getLocation());
        //verifier l'initialisation avant la definition de la variable
        
        varName.setDefinition(varDef); // deco
        try {
            localEnv.declare(varName.getName(), varDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Variable " + varName.getName().getName() + " deja definie", varName.getLocation());
        }
        
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.print(";");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    protected void codeGenDeclVar(DecacCompiler compiler) {

        RegisterOffset addr = compiler.getStackManager().allocVar();

        varName.getExpDefinition().setOperand(addr);

        initialization.codeGenInitialization(compiler, addr);
    }

    @Override
    public AbstractIdentifier getVarName()
    {
        return varName;
    }


}
