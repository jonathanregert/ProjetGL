package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.AbstractOpCmp;
import fr.ensimag.deca.tree.Lower;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class TestComparison {

    DecacCompiler compiler = new DecacCompiler(null, null);
    Type INT = new IntType(null);
    Type FLOAT = new FloatType(null);

    @Test
    public void testIntLessInt() throws ContextualError {
        AbstractExpr leftOperand = Mockito.mock(AbstractExpr.class);
        when(leftOperand.verifyExpr(compiler, null, null)).thenReturn(INT);
        AbstractExpr rightOperand = Mockito.mock(AbstractExpr.class);
        when(rightOperand.verifyExpr(compiler, null, null)).thenReturn(INT);

        // INT < INT
        AbstractOpCmp cmp = new Lower(leftOperand, rightOperand);
        Type typeExpr = cmp.verifyExpr(compiler, null, null);

        assertTrue(typeExpr.isBoolean(), "Le type devait être un bool");
    }

    @Test
    public void testIntFloat() throws ContextualError {
        AbstractExpr leftOperand = Mockito.mock(AbstractExpr.class);
        when(leftOperand.verifyExpr(compiler, null, null)).thenReturn(INT);
        AbstractExpr rightOperand = Mockito.mock(AbstractExpr.class);
        when(rightOperand.verifyExpr(compiler, null, null)).thenReturn(FLOAT);

        // INT < FLOAT
        AbstractOpCmp cmp = new Lower(leftOperand, rightOperand);
        Type typeExpr = cmp.verifyExpr(compiler, null, null);

        assertTrue(typeExpr.isBoolean(), "Le type devait être un bool");
        assertTrue(cmp.getLeftOperand() instanceof ConvFloat, "L'enrichissement sur ConvFloat n'est pas correct");

        // check that the mocks have been called properly.
        verify(leftOperand).verifyExpr(compiler, null, null);
        verify(rightOperand).verifyExpr(compiler, null, null);
    }

    @Test
    public void testFloatInt() throws ContextualError {
        AbstractExpr leftOperand = Mockito.mock(AbstractExpr.class);
        when(leftOperand.verifyExpr(compiler, null, null)).thenReturn(FLOAT);
        AbstractExpr rightOperand = Mockito.mock(AbstractExpr.class);
        when(rightOperand.verifyExpr(compiler, null, null)).thenReturn(INT);

        // FLOAT < INT
        AbstractOpCmp cmp = new Lower(leftOperand, rightOperand);
        Type typeExpr = cmp.verifyExpr(compiler, null, null);

        assertTrue(typeExpr.isBoolean(), "Le type devait être un bool");
        assertTrue(cmp.getRightOperand() instanceof ConvFloat, "L'enrichissement ConvFloat devait être sur l'opérande gauche");

        // check that the mocks have been called properly.
        verify(leftOperand).verifyExpr(compiler, null, null);
        verify(rightOperand).verifyExpr(compiler, null, null);
    }

    @Test
    public void testStringInt() throws ContextualError {
        AbstractExpr leftOperand = Mockito.mock(AbstractExpr.class);
        when(leftOperand.verifyExpr(compiler, null, null)).thenReturn(new StringType(null));
        AbstractExpr rightOperand = Mockito.mock(AbstractExpr.class);
        when(rightOperand.verifyExpr(compiler, null, null)).thenReturn(INT);

        // StringType < INT
        AbstractOpCmp cmp = new Lower(leftOperand, rightOperand);

        assertThrows(ContextualError.class,
            () -> cmp.verifyExpr(compiler, null, null),
            "verifyExpr aurait du lever une ContextualError Exception");
        
        // check that the mocks have been called properly.
        verify(leftOperand).verifyExpr(compiler, null, null);
        verify(rightOperand).verifyExpr(compiler, null, null);
    }
}