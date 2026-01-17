package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * @author gl42
 * @date 01/01/2026
 */
public class DeclParam extends AbstractDeclParam {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier varName) {
        Validate.notNull(type);
        Validate.notNull(varName);
        this.type = type;
        this.varName = varName;
    }

    public AbstractIdentifier getVarName(){
        return varName;
    }

    @Override
    protected void verifyDeclParam(DecacCompiler compiler,
            EnvironmentExp localEnv)
            throws ContextualError {
        
        Type t = verifyDeclParamType(compiler);

        ParamDefinition paramDef = new ParamDefinition(t, getLocation());
        
        try {
            localEnv.declare(varName.getName(), paramDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Paramètre '" + varName.getName().getName() + "' déjà défini", varName.getLocation());
        }

        varName.setDefinition(paramDef);
    }


    @Override
    public Type verifyDeclParamType(DecacCompiler compiler) throws ContextualError {
        Type t = type.verifyType(compiler);
        if (t.isVoid()) {
            throw new ContextualError("Type void interdit pour un paramètre", getLocation());
        }
        return t;
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
    }


}
