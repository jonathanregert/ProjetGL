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
        when(mockOperand.verifyExpr(any(), any(), any()))
            .thenReturn(compiler.environmentType.BOOLEAN); // On impose un type invalide, dans ce cas Boolean

        // On vérifie que verifyExpr lève bien l'erreur attendue
        assertThrows(ContextualError.class, () -> {
            convFloat.verifyExpr(compiler, null, null);
        });
        
        // Vérification Mockito : l'opérande doit avoir été vérifié une fois
        verify(mockOperand, times(1)).verifyExpr(any(), any(), any());
    }
}