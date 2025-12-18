package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.And;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class TestBooleanOp {

    DecacCompiler compiler = new DecacCompiler(null, null);
    Type BOOL = new BooleanType(null);

    @Test
    public void testBoolBool() throws ContextualError {
        AbstractExpr leftOperand = Mockito.mock(AbstractExpr.class);
        when(leftOperand.verifyExpr(compiler, null, null)).thenReturn(BOOL);
        AbstractExpr rightOperand = Mockito.mock(AbstractExpr.class);
        when(rightOperand.verifyExpr(compiler, null, null)).thenReturn(BOOL);

        // BOOL && BOOL
        And and = new And(leftOperand, rightOperand);
        Type typeExpr = and.verifyExpr(compiler, null, null);

        assertTrue(typeExpr.isBoolean(), "Le type devait être un bool");

        // check that the mocks have been called properly.
        verify(leftOperand).verifyExpr(compiler, null, null);
        verify(rightOperand).verifyExpr(compiler, null, null);
    }

    @Test
    public void testIntBool() throws ContextualError {
        AbstractExpr leftOperand = Mockito.mock(AbstractExpr.class);
        when(leftOperand.verifyExpr(compiler, null, null)).thenReturn(new IntType(null));
        AbstractExpr rightOperand = Mockito.mock(AbstractExpr.class);
        when(rightOperand.verifyExpr(compiler, null, null)).thenReturn(BOOL);

        // INT && BOOL
        And and = new And(leftOperand, rightOperand);

        assertThrows(ContextualError.class,
            () -> and.verifyExpr(compiler, null, null) ,
            "verifyExpr aurait du lever une ContextualError Exception");

        // check that the mocks have been called properly.
        verify(leftOperand).verifyExpr(compiler, null, null);
        verify(rightOperand).verifyExpr(compiler, null, null);
    }
}