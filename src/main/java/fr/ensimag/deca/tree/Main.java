package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;


import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
/**
 * @author gl42
 * @date 01/01/2026
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;
    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        // A FAIRE: Appeler méthodes "verify*" de ListDeclVarSet et ListInst.
        // Vous avez le droit de changer le profil fourni pour ces méthodes
        // (mais ce n'est à priori pas nécessaire).
        Type voidType = compiler.environmentType.VOID; // main est tjrs void
        EnvironmentExp envExp = new EnvironmentExp(null);
        this.declVariables.verifyListDeclVariable(compiler, envExp, null);
        this.insts.verifyListInst(compiler, envExp, null, voidType);
        LOG.debug("verify Main: end");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        // A FAIRE: traiter les déclarations de variables.
        
        compiler.addComment("Beginning of main instructions:");

        int nb = 0;
        for (AbstractDeclVar decl : declVariables.getList()) {
            VariableDefinition def =
                (VariableDefinition) decl.getVarName().getExpDefinition();

            def.setOperand(new RegisterOffset(nb + 1, Register.LB)); // 1(GB), 2(GB), ...
            nb++;
        }
        // Réserve espace dans pile pour variables non initialisées
        compiler.addInstruction(new ADDSP(new ImmediateInteger(nb)));

        declVariables.codeGenListDeclVar(compiler);

        insts.codeGenListInst(compiler);
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
 
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}
