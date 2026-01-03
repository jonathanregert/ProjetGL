package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ConvFloat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

// généré par le chatgpt pour verdir le test de ConvFloat

public class TestConvFloat {

    @Mock
    private AbstractExpr mockOperand;
    
    private DecacCompiler compiler;
    private ConvFloat convFloat;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        compiler = new DecacCompiler(null, null);
        convFloat = new ConvFloat(mockOperand);
    }

    @Test
    void testVerifyExprInvalid() throws ContextualError {
        // On simule un opérande qui retourne un BOOLEAN au lieu d'un INT
        when(mockOperand.verifyExpr(any(), any(), any()))
            .thenReturn(compiler.environmentType.BOOLEAN);

        // On vérifie que verifyExpr lève bien la ContextualError
        assertThrows(ContextualError.class, () -> {
            convFloat.verifyExpr(compiler, null, null);
        });
        
        // Vérification Mockito : l'opérande doit avoir été vérifié une fois
        verify(mockOperand, times(1)).verifyExpr(any(), any(), any());
    }

    @Test
    void testVerifyExprValid() throws ContextualError {
        // Cas normal : l'opérande est bien un INT
        when(mockOperand.verifyExpr(any(), any(), any()))
            .thenReturn(compiler.environmentType.INT);

        Type result = convFloat.verifyExpr(compiler, null, null);

        // On vérifie que le type retourné est bien FLOAT
        assertTrue(result.isFloat());
        assertEquals(compiler.environmentType.FLOAT, convFloat.getType());
    }
}