package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;



/**
 * Deca Identifier
 *
 * @author gl42
 * @date 01/01/2026
 */
public class Selection extends AbstractLValue{
    private final AbstractExpr object;
    private final AbstractIdentifier field;

    public Selection(AbstractExpr object, AbstractIdentifier field) {
        Validate.notNull(object);
        Validate.notNull(field);
        this.object = object;
        this.field = field;
    }

    public AbstractExpr getObject() {
        return object;
    }

    public AbstractIdentifier getField() {
        return field;
    }

    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type objectType = object.verifyExpr(compiler, localEnv, currentClass);

        if (!objectType.isClass()) {
            throw new ContextualError("Sélection impossible sur un type non-objet", object.getLocation());
        }

        ClassDefinition classDef = ((ClassType) objectType).getDefinition();

        Symbol fieldSym = field.getName(); 
        ExpDefinition memberDef = classDef.getMembers().get(fieldSym);

        if (memberDef == null || !memberDef.isField()) {
            throw new ContextualError("Le membre " + fieldSym + " n'existe pas dans " + objectType, field.getLocation());
        }

        // verif de la visibilité
        FieldDefinition fieldDef = (FieldDefinition) memberDef;
        if (fieldDef.getVisibility() == Visibility.PROTECTED){
            // cond 2 : il faut qu'on soit dans une sous-classe de la classe où est défini le champ
            if (currentClass == null) {
                throw new ContextualError("Accès à un champ protégé '" + fieldSym + "' interdit dans le Main", field.getLocation());
            }
            if (!currentClass.getType().isSubtype(compiler.environmentType, fieldDef.getContainingClass().getType())) {
                throw new ContextualError("La classe actuelle " + currentClass.getType() + " n'a pas accès au champ protégé de " + fieldDef.getContainingClass().getType(), field.getLocation());
            }
            //cond 1 : l'objet doit être sous type de la classe où est défini le champ
            if (!objectType.isSubtype(compiler.environmentType, currentClass.getType())) {
                throw new ContextualError("Accès protégé invalide : le type de l'objet (" + objectType + ") n'est pas un sous-type de la classe courante (" + currentClass.getType() + ")", object.getLocation());
            }
        }
        field.setDefinition(memberDef);
        setType(memberDef.getType());

        return getType();
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        object.iter(f);
        field.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        object.prettyPrint(s, prefix, false);
        field.prettyPrint(s, prefix, true);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        object.decompile(s);
        s.print(".");
        field.decompile(s);
    }

    @Override
    public DAddr codeGenAddr(DecacCompiler compiler) {
        getObject().codeGenExpr(compiler, Register.getR(2));

        compiler.addInstruction(new CMP(new NullOperand(), Register.getR(2)));
        compiler.addInstruction(new BEQ(
            compiler.getErrorManager().label(ErrorManager.RuntimeError.NULL_DEREFERENCE)
        ));

        FieldDefinition fd = getField().getFieldDefinition();
        return new RegisterOffset(fd.getIndex(), Register.getR(2));
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target){
        DAddr addr = codeGenAddr(compiler);
        compiler.addInstruction(new LOAD(addr, target));
    }
}