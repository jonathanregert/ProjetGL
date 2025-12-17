lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}

// Littéraux et mots-clés
TRUE        : 'true';
FALSE       : 'false';
THIS        : 'this';
NULL        : 'null';

IF          : 'if';
ELSE        : 'else';
WHILE       : 'while';
RETURN      : 'return';

PRINT       : 'print';
PRINTLN     : 'println';
PRINTX      : 'printx';
PRINTLNX    : 'printlnx';
READINT     : 'readInt';
READFLOAT   : 'readFloat';

CLASS       : 'class';
EXTENDS     : 'extends';
PROTECTED   : 'protected';
NEW         : 'new';
INSTANCEOF  : 'instanceof';
ASM         : 'asm';

// Opérateurs MULTI-caractères

OR      : '||';
AND     : '&&';

EQEQ    : '==';
NEQ     : '!=';
LEQ     : '<=';
GEQ     : '>=';

// Opérateurs MONO-caractères

PLUS    : '+';
MINUS   : '-';
TIMES   : '*';
SLASH   : '/';
PERCENT : '%';

LT      : '<';
GT      : '>';
EXCLAM  : '!';
EQUALS  : '=';

// Délimiteurs

OBRACE  : '{';
CBRACE  : '}';
OPARENT : '(';
CPARENT : ')';
SEMI    : ';';
COMMA   : ',';
COLON   : ':';
DOT     : '.';

// Littéraux numériques et chaînes

fragment DIGIT : [0-9];
fragment LETTER : [a-zA-Z];

INT : '0' | [1-9][0-9]*;

FLOAT
    : DIGIT+ '.' DIGIT+ ([eE] [+-]? DIGIT+)? [fF]?
    | ('0x' | '0X') [0-9a-fA-F]+ '.' [0-9a-fA-F]+ [pP] [+-]? DIGIT+ [fF]?
    ;

STRING : '"' (~["\r\n\\] | '\\' .)* '"';

MULTI_LINE_STRING
    : '"' ( ~["\\] | '\\' . | '\n' | '\r' )* '"'
    ;

// IDENT

IDENT : (LETTER | '_' | '$') (LETTER | DIGIT | '_' | '$')*;

// INCLUDE

INCLUDE : '#include' [ \t]* '"' FILENAME '"';
FILENAME : [a-zA-Z0-9._-]+;

// Commentaires et espaces

LINE_COMMENT : '//' ~[\r\n]* -> skip;
BLOCK_COMMENT : '/*' .*? '*/' -> skip;
WS : [ \t\r\n]+ -> skip;

// // Deca lexer rules.
// DUMMY_TOKEN: .; // A FAIRE : Règle bidon qui reconnait tous les caractères.
//                 // A FAIRE : Il faut la supprimer et la remplace&r par les vraies règles.
