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
		new Program();
		Code.output();
	}
}

class Program {

	Decls d;
	Stmts s;
	
	Program() {
		d = new Decls();
		s = new Stmts();
		if(Lexer.nextToken == Token.KEY_END) {
			Code.gen("return", 0);
		}
	}
	 
}

class Decls {
	
	Idlist i;
	
	Decls() {
		if(Lexer.nextToken == Token.KEY_INT) {
			Lexer.lex();
			i = new Idlist();
		}
	}
}

class Idlist {
	
	Idlist() {
		
		while(Lexer.nextToken == Token.ID) {

			Code.index("push_id", Lexer.ident);
			Lexer.lex();
			
			if(Lexer.nextToken == Token.COMMA) {
				Lexer.lex();
			}
			else if(Lexer.nextToken == Token.SEMICOLON) {
				Lexer.lex();
				break;
			}
		}
	}
	
}

class Stmt {
	
	Assign a;
	Loop l;
	Cond c;
	
	Stmt() {
		switch(Lexer.nextToken) {
		//LOOP
		case Token.KEY_WHILE:
			l = new Loop();
			break;
			
		//ASSIGN	
		case Token.ID:
			a = new Assign();
			break;
		
		//COND
		case Token.KEY_IF:
			c = new Cond();
			break;
		default:
			System.exit(1);
		}
	}
} 

class Stmts {
	
	Stmt s;
	Stmts ss;
	
	Stmts() {

		s = new Stmt();
		
		if(Lexer.nextToken != Token.KEY_END && Lexer.nextToken != Token.RIGHT_BRACE) {
			ss = new Stmts();
		}
	}
	
}

class Assign {
	
	Expr e;
	
	Assign() {
		char ident = 0;
		if(Lexer.nextToken == Token.ID) {
			ident = Lexer.ident;
			Lexer.lex();
		}
		
		if(Lexer.nextToken == Token.ASSIGN_OP) {
			Lexer.lex();
		}
		
		e = new Expr();
		
		int i = Code.index("get_index", ident);
		if(i != -1) {
			Code.gen("store", i);	
		}
		else {
			System.exit(1);
		}
		
		if(Lexer.nextToken == Token.SEMICOLON) {
			Lexer.lex();
		}
	}
}

class Cond {
	
	Rexpr r;
	Cmpdstmt c1, c2;
	
	Cond() {
		
		if(Lexer.nextToken == Token.KEY_IF) {
			Lexer.lex();
		}
		
		if(Lexer.nextToken == Token.LEFT_PAREN) {
			Lexer.lex();
		}
		
		r = new Rexpr();
		
		Code.skip(3);
		
		if(Lexer.nextToken == Token.RIGHT_PAREN) {
			Lexer.lex();
		}
		
		c1 = new Cmpdstmt();
		
		
		
		if(Lexer.nextToken == Token.KEY_ELSE) {
			Code.patch(3);
			Code.gen("goto", -1);
			Code.skip(3);
			
			Lexer.lex();
			c2 = new Cmpdstmt();
			
			Code.patch(0);
		}
		else {
			Code.patch(0);
		}
	}
}

class Loop {
	
	Rexpr r;
	Cmpdstmt c;
	
	Loop() {

		int startptr = Code.cdptr;
		if(Lexer.nextToken == Token.KEY_WHILE) {
			Lexer.lex();
		}
		
		if(Lexer.nextToken == Token.LEFT_PAREN) {
			Lexer.lex();
		}
		
		r = new Rexpr();
		
		Code.skip(3);
		
		if(Lexer.nextToken == Token.RIGHT_PAREN) {
			Lexer.lex();
		}
		
		c = new Cmpdstmt();
		
		Code.gen("goto", startptr);
		Code.patch(0);
	}
}

class Cmpdstmt {
	
	Stmts s;
	
	Cmpdstmt() {
		if(Lexer.nextToken == Token.LEFT_BRACE) {
			Lexer.lex();
		}
		s = new Stmts();
		if(Lexer.nextToken == Token.RIGHT_BRACE) {
			Lexer.lex();
		}
	}
}

class Rexpr {
	
	Expr e1,e2;
	
	Rexpr() {
		e1 = new Expr();
		int op = -1;
		
		if(Lexer.nextToken == Token.LESSER_OP || Lexer.nextToken == Token.GREATER_OP || Lexer.nextToken == Token.ASSIGN_OP || Lexer.nextToken == Token.NOT_EQ) {
			op = Lexer.nextToken;
			Lexer.lex();
		}
		e2 = new Expr();
		
		switch(op) {
		case Token.LESSER_OP:
			Code.gen("lesser_op", 0);
			break;
		case Token.GREATER_OP:
			Code.gen("greater_op", 0);
			break;
		case Token.ASSIGN_OP:
			Code.gen("assign_op", 0);
			break;
		case Token.NOT_EQ:
			Code.gen("not_eq", 0);
			break;
		}
	}
}

class Expr {  
	
	Expr e;
	Term t;
	
	Expr() {
		t = new Term();
		int op = -1;
		
		if(Lexer.nextToken == Token.ADD_OP || Lexer.nextToken == Token.SUB_OP) {
			op = Lexer.nextToken;
			Lexer.lex();
			e = new Expr();
		}
		
		if(op == Token.ADD_OP) {
			Code.gen("arith_op", 0);
		}
		else if(op == Token.SUB_OP) {
			Code.gen("arith_op", 1);
		}
	}
}

class Term {  
	
	Term t;
	Factor f;
	
	Term() {
		f = new Factor();
		int op = -1;
		
		if(Lexer.nextToken == Token.MULT_OP || Lexer.nextToken == Token.DIV_OP) {
			op = Lexer.nextToken;
			Lexer.lex();
			t = new Term();
		}
		
		if(op == Token.MULT_OP) {
			Code.gen("arith_op", 2);
		}
		else if(op == Token.DIV_OP) {
			Code.gen("arith_op", 3);
		}
	}
}

class Factor {  
	
	Expr e;
	
	Factor() {

		switch(Lexer.nextToken) {
		case Token.INT_LIT:
			Code.gen("push_int", Lexer.intValue);
			Lexer.lex();
			break;
			
		case Token.ID:
			int index = Code.index("get_index", Lexer.ident);
			if(index != -1) {				
				Code.gen("load", index);
				Lexer.lex();
			}
			else {
				System.exit(1);
			}
			break;
			
		case Token.LEFT_PAREN:
			Lexer.lex();
			e = new Expr();
			
			if(Lexer.nextToken == Token.RIGHT_PAREN) {
				Lexer.lex();
			}
			
			break;
		}
	}
}

class Code {

	static String code[] = new String[500];
	static int cdptr = 0;
	static int skipstack[] = new int[100];
	static int skipptr = -1;
	static char localVars[] = new char[100];
	static int lvptr = -1;
	
	public static void output() {
		for(int i = 0; i <= cdptr; i++) {
			if(code[i] != null) {
				System.out.println(code[i]);
			}		
		}
	}
	
	public static void gen(String op, int value) {
		switch(op) {
		case "push_int":
			if(value <= 5) {
				code[cdptr] = cdptr + ": " + "iconst_" + value;
				cdptr++;
				break;
			}
			else if(value <= 127) {
				code[cdptr] = cdptr + ": " + "bipush " + value;
				cdptr+=2;
				break;
			}
			else {
				code[cdptr] = cdptr + ": " + "sipush " + value;
				cdptr+=3;
				break;
			}
		case "store":
			if(value <= 3) {
				code[cdptr] = cdptr + ": " + "istore_" + value;
				cdptr++;
			}
			else {
				code[cdptr] = cdptr + ": " + "istore " + value;
				cdptr+=2;
			}
			break;
		case "load":
			if(value <= 3) {
				code[cdptr] = cdptr + ": " + "iload_" + value;
				cdptr++;
			}
			else {
				code[cdptr] = cdptr + ": " + "iload " + value;
				cdptr+=2;
			}
			break;
		case "arith_op":
			switch(value) {
			case 0:
				code[cdptr] = cdptr + ": " + "iadd";
				cdptr++;
				break;
			case 1:
				code[cdptr] = cdptr + ": " + "isub";
				cdptr++;
				break;
			case 2:
				code[cdptr] = cdptr + ": " + "imul";
				cdptr++;
				break;
			case 3:
				code[cdptr] = cdptr + ": " + "idiv";
				cdptr++;
				break;
			}
			break;
		case "lesser_op":
			code[cdptr] = cdptr + ": " + "if_icmpge ";
			break;
		case "greater_op":
			code[cdptr] = cdptr + ": " + "if_icmple ";
			break;
		case "assign_op":
			code[cdptr] = cdptr + ": " + "if_icmpne ";
			break;
		case "not_eq":
			code[cdptr] = cdptr + ": " + "if_icmpeq ";
			break;
		case "goto":
			if(value >= 0) {
				code[cdptr] = cdptr + ": " + "goto " + value;
				cdptr+=3;
			}
			else {
				code[cdptr] = cdptr + ": " + "goto ";
			}
			break;
		case "return":
			code[cdptr] = cdptr + ": " + "return";
			break;
		}
	}
	
	public static int index(String op, char ident) {
		switch(op) {
		case "get_index":
			for(int i = 0; i <= lvptr; i++) {
				if(localVars[i] == ident) {
					return i;
				}
			}
			return -1;
		case "push_id":
			if(lvptr < 100) {
				lvptr++;
				localVars[lvptr] = ident;
				return 1;
			}
			else
				return -1;
		}
		
		return -1;
	}
	
	public static void skip(int numOfBytes) {
		skipstack[++skipptr] = cdptr;
		cdptr += numOfBytes;
	}
	
	public static void patch(int offset) {
		code[skipstack[skipptr]] += cdptr + offset;
		skipptr--;
	}
}