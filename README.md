Bytecode-Generator
==================

This is a Java program that generates bytecode for any program written in a small grammar that includes statements, expressions, logical and conditional constructs and loops.

This was done as an assignment in my CSE 505 Fundamentals of Programming Languages class taken by Professor Bharat Jayaraman at the University of Buffalo, with skeleton code for the Buffer, Lexer and Token provided.

Grammar for TinyPL (using EBNF notation) is as follows:

 program ->  decls stmts end
 decls   ->  int idlist ;
 idlist  ->  id { , id } 
 stmts   ->  stmt [ stmts ]
 cmpdstmt->  '{' stmts '}'
 stmt    ->  assign | cond | loop
 assign  ->  id = expr ;
 cond    ->  if '(' rexp ')' cmpdstmt [ else cmpdstmt ]
 loop    ->  while '(' rexp ')' cmpdstmt  
 rexp    ->  expr (< | > | =) expr
 expr    ->  term   [ (+ | -) expr ]
 term    ->  factor [ (* | /) term ]
 factor  ->  int_lit | id | '(' expr ')'
 
Lexical:   id is a single character; 
	      int_lit is an unsigned integer;
		 equality operator is =, not ==

Sample Program: Factorial
 
int n, i, f;
n = 4;
i = 1;
f = 1;
while (i < n) {
  i = i + 1;
  f= f * i;
}
end

Sample Output bytecode for the Factorial program:

0: iconst_4
1: istore_0
2: iconst_1
3: istore_1
4: iconst_1
5: istore_2
6: iload_1
7: iload_0
8: if_icmpge 22
11: iload_1
12: iconst_1
13: iadd
14: istore_1
15: iload_2
16: iload_1
17: imul
18: istore_2
19: goto 6
22: return
