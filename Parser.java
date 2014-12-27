/* 		OO PARSER AND BYTE-CODE GENERATOR FOR TINY PL
 
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

   Sample Program:  GCD
   
int x, y;
x = 121;
y = 132;
while (x != y) {
  if (x > y) 
       { x = x - y; }
  else { y = y - x; }
}
end

 */

public class Parser {
	public static void main(String[] args)  {
		System.out.println("Enter program and terminate with 'end'!\n");
		Lexer.lex();
		Program p = new Program();
		p.program();
		Code.output();
	}
}

class Program {
	 
}

class Decls {
	 
}

class Idlist {
	 
}

class Stmt {
	 
} 

class Stmts {
	 
}

class Assign {
	 
}

class Cond {
	 
}

class Loop {
	 
}

class Cmpdstmt {
	 
}

class Rexpr {
	 
}

class Expr {  
	 
}

class Term {  
	 
}

class Factor {  
	 
}

class Code {
	 
}


