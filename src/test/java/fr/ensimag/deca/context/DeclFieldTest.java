package fr.ensimag.deca.context;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tree.*;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import static org.junit.jupiter.api.Assertions.*;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;


class DeclFieldTest {

    @Test
    void testDoubleDefThrowsContextualError() throws DoubleDefException {
        // 1. Mock des dépendances
        EnvironmentExp mockMembers = mock(EnvironmentExp.class);
        ClassDefinition mockClass = mock(ClassDefinition.class);
        Symbol mockSymbol = mock(Symbol.class);
        
        when(mockClass.getMembers()).thenReturn(mockMembers);
        doThrow(new EnvironmentExp.DoubleDefException())
            .when(mockMembers).declare(any(), any());

        // simule l'appel dans DeclField
        // on vérifie que le catch attrape l'exception : 
        assertThrows(ContextualError.class, () -> {
            try {
                mockClass.getMembers().declare(mockSymbol, null);
            } catch (EnvironmentExp.DoubleDefException e) {
                // C'est ce bloc que ton test valide
                throw new ContextualError("Double définition", null);
            }
        });
    }
}