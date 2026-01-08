package fr.ensimag.deca.tree;
import fr.ensimag.deca.tree.AbstractLValue;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;


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

        ExpDefinition memberDef = classDef.getMembers().get(field.getName());
        if (memberDef == null) {
            throw new ContextualError("Le membre " + field.getName() + " n'existe pas dans " + objectType, field.getLocation());
        }

        field.setDefinition(memberDef); 
        setType(memberDef.getType());   

        return getType();
    }
    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     */
   
    


    @Override
    protected void iterChildren(TreeFunction f) {
        object.iter(f);
        field.iter(f);
    }

    @Override
    protected DAddr codeGenAddr(DecacCompiler compiler) {
        object.codeGenInst(compiler);
        FieldDefinition fieldDef = field.getFieldDefinition();
        return new RegisterOffset(fieldDef.getIndex(), Register.R1);
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
    protected void codeGenInst(DecacCompiler compiler) {
        //compiler.addInstruction(
            //new LOAD(
          //      getExpDefinition().getOperand(),
             //   Register.R1
         //   )
    //   );
    }

}