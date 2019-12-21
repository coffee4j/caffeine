grammar CtwFile;

model:
    MODEL_START modelId=IDENTIFIER
        PARAMETERS_START (parameters+=parameter)+ END?
        (CONSTRAINTS_START (constraints+=constraint)+)? END?
        (SCENARIOS_START (scenarios+=scenario)+)? END?
    EOF;

parameter:
    booleanParameter
    | enumParameter
    | rangeParameter
    ;

booleanParameter: (id=IDENTIFIER COLON BOOL SEMICOLON?)
    | (BOOL id=IDENTIFIER SEMICOLON?);
enumParameter: (id=IDENTIFIER COLON LBRACE (element COMMA?)+ RBRACE SEMICOLON?)
    | (ENUMERATIVE id=IDENTIFIER LBRACE (element COMMA?)+ RBRACE SEMICOLON?);
rangeParameter: id=IDENTIFIER COLON LBRACKET start=sgnNumber '..' end=sgnNumber RBRACKET SEMICOLON?;

sgnNumber: MINUS? NUMBER;
element: (IDENTIFIER DOT)?(IDENTIFIER | NUMBER IDENTIFIER | STRING | MINUS? NUMBER);

constraint: (id=IDENTIFIER COLON)? HASH constraintExpression HASH;

constraintExpression:
    LPAREN constraintExpression RPAREN # ParenExpression
    | left=element op=(GE | GT | LE | LT | EQ | NEQ) right=element # ElementExpression
    | element # BooleanAtomExpression
    | NOT constraintExpression # NotExpression
    | left=constraintExpression AND right=constraintExpression # AndExpression
    | left=constraintExpression OR right=constraintExpression # OrExpression
    | left=constraintExpression op=(IMPLIES | IFF) right=constraintExpression # ImpliesExpression;

scenario: (id=IDENTIFIER COLON)? HASH strength=NUMBER LPAREN (faults+=IDENTIFIER COMMA?)+
 RPAREN (LPAREN ((constraints+=IDENTIFIER COMMA?)+ | allConstraints=STAR)RPAREN)? HASH;

END: 'end';
ENUMERATIVE: 'Enumerative' | 'Numbers';
MODEL_START: 'Model';
PARAMETERS_START: 'Parameters:';
FAULTS_START: 'Faults';
CONSTRAINTS_START: 'Constraints:';
SCENARIOS_START: 'Scenarios:';
STRENGTH: 'strength';
FAULTS: 'faults';
CONSTRAINTS: 'constraints';

STAR: '*';
DOT: '.';
IMPLIES: '=>' | '->';
IFF: '<=>' | '<->';
BOOL:'Boolean';
LPAREN: '(';
RPAREN: ')';
LBRACKET: '[';
RBRACKET: ']';
LBRACE: '{';
RBRACE: '}';
RANGE: '..';
AND: '&&' | 'and' | 'AND';
OR: '||' | 'or' | 'OR';
NOT: '!' | 'not' | 'NOT';
HASH: '#';
MINUS: '-';
SEMICOLON: ';';
COLON: ':';
GE: '>=';
GT: '>';
LE: '<=';
LT: '<';
NEQ: '!=';
EQ: '==' | '=';
COMMA: ',';
NUMBER: ('0'..'9')+;
STRING: '"' ( '\\' [btnfr"'\\] | ~[\r\n\\"] )* '"';
IDENTIFIER: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')*;
COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;
WS : [ \t\r\n]+ -> skip ;
ERROR: .;


