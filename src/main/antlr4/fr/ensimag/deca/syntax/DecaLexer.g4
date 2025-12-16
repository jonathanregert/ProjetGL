lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
   private java.util.Stack<IncludeSaveStruct> includes = new java.util.Stack<>();

   @Override
   public Token nextToken() {
   try {
      return super.nextToken();} catch (SkipANTLRPostAction e) {
         // Après un #include, on continue immédiatement
         return nextToken();}
   }
}

fragment DIGIT : '0' .. '9';
SIGN : '+' | '-';
EXP : ('E' | 'e' ) SIGN NUM;
DEC : NUM '.' NUM;
FLOATDEC : (DEC | DEC EXP) ('F' | 'f');
DIGITHEX : DIGIT | 'A' .. 'F' | 'a' .. 'f';
NUMHEX : DIGITHEX+ ;
FLOATHEX : ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN NUM ('F' | 'f');
FLOAT : FLOATHEX | FLOATDEC;
fragment LETTER : 'a' .. 'z' | 'A' .. 'Z';
IDENT : (LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;
fragment POSITIVE_DIGIT : '1' .. '9';
INT : '0' | POSITIVE_DIGIT DIGIT*;
fragment STRING_CAR : ~[\"\\\r\n];
EOL : '\n' | '\r\n' | '\r';
STRING : '"' (STRING_CAR | '\\"' | '\\\\')* '"';
MULTI_LINE_STRING : '"' (STRING_CAR | EOL | '\\"' | '\\\\')* '"';
COMMENT : '/*' .*? '*/';
FILENAME : (LETTER | DIGIT | '.' | '-' | '_')+;

//l'inclusion de fichiers
INCLUDE : '#include' (' ')* '"' FILENAME '"'{
   doInclude(getText());
   };

OBRACE   : '{' ;
CBRACE   : '}' ;
OPARENT  : '(' ;
CPARENT  : ')' ;
SEMI     : ';' ;
COMMA    : ',' ;
DOT      : '.' ;
EQUALS   : '=' ;

PLUS     : '+' ;
MINUS    : '-' ;
TIMES    : '*' ;
SLASH    : '/' ;
PERCENT  : '%' ;

OR       : '||' ;
AND      : '&&' ;
EXCLAM   : '!' ;

EQEQ     : '==' ;
NEQ      : '!=' ;
LEQ      : '<=' ;
GEQ      : '>=' ;
LT       : '<' ;
GT       : '>' ;
INSTANCEOF : 'instanceof' ;

IF       : 'if' ;
ELSE     : 'else' ;
WHILE    : 'while' ;
RETURN   : 'return' ;

PRINT    : 'print' ;
PRINTLN  : 'println' ;
PRINTX   : 'printx' ;
PRINTLNX : 'printlnx' ;

READINT     : 'readInt' ;
READFLOAT   : 'readFloat' ;
NEW         : 'new' ;
CLASS       : 'class' ;
EXTENDS     : 'extends' ;
PROTECTED   : 'protected' ;
ASM         : 'asm' ;

TRUE        : 'true' ;
FALSE       : 'false' ;
THIS        : 'this' ;
NULL        : 'null' ;

S  :   ( ' ' | '\t'| '\r'| '\n' | COMMENT) {
   skip(); // avoid producing a token
   };


// // Deca lexer rules.
// DUMMY_TOKEN: .; // A FAIRE : Règle bidon qui reconnait tous les caractères.
//                 // A FAIRE : Il faut la supprimer et la remplace&r par les vraies règles.
