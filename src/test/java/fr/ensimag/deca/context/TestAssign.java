package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractLValue;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.Assign;
import fr.ensimag.deca.tree.ConvFloat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class TestAssign {

    DecacCompiler compiler = new DecacCompiler(null, null);

    Type INT = new IntType(null);
    Type FLOAT = new FloatType(null);

    @Test
    public void testIntInt() throws ContextualError {
        AbstractLValue leftOperand = Mockito.mock(AbstractLValue.class);
        when(leftOperand.verifyExpr(compiler, null, null)).thenReturn(INT);
        AbstractExpr rightOperand = Mockito.mock(AbstractExpr.class);
        when(rightOperand.verifyExpr(compiler, null, null)).thenReturn(INT);

        // INT = INT
        Assign a = new Assign(leftOperand, rightOperand);
        Type typeExpr = a.verifyExpr(compiler, null, null);
        
        assertTrue(typeExpr.isInt());

        // check that the mocks have been called properly.
        verify(leftOperand).verifyExpr(compiler, null, null);
        verify(rightOperand).verifyExpr(compiler, null, null);
    }

    @Test
    public void testFloatInt() throws ContextualError {
        AbstractLValue leftOperand = Mockito.mock(AbstractLValue.class);
        when(leftOperand.verifyExpr(compiler, null, null)).thenReturn(FLOAT);
        AbstractExpr rightOperand = Mockito.mock(AbstractExpr.class);
        when(rightOperand.verifyExpr(compiler, null, null)).thenReturn(INT);

        // FLOAT = INT
        Assign a = new Assign(leftOperand, rightOperand);
        Type typeExpr = a.verifyExpr(compiler, null, null);

        assertTrue(typeExpr.isFloat(), "Le type devait être un float");
        assertTrue(a.getRightOperand() instanceof ConvFloat, "L'enrichissement ConvFloat n'est pas correct");

        // check that the mocks have been called properly.
        verify(leftOperand).verifyExpr(compiler, null, null);
        verify(rightOperand).verifyExpr(compiler, null, null);
    }

    @Test
    public void testIntFloat() throws ContextualError {
        AbstractLValue leftOperand = Mockito.mock(AbstractLValue.class);
        when(leftOperand.verifyExpr(compiler, null, null)).thenReturn(INT);
        AbstractExpr rightOperand = Mockito.mock(AbstractExpr.class);
        when(rightOperand.verifyExpr(compiler, null, null)).thenReturn(FLOAT);

        // INT = FLOAT
        Assign a = new Assign(leftOperand, rightOperand);
        
        // Ne devrait pas marcher
        // On vérifie que l'instruction
        // lève une ContextualError Exception
        assertThrows(ContextualError.class,
            () -> a.verifyExpr(compiler, null, null),
            "verifyExpr aurait du lever une ContextualError Exception");

        // check that the mocks have been called properly.
        verify(leftOperand).verifyExpr(compiler, null, null);
        verify(rightOperand).verifyExpr(compiler, null, null);
    }
}