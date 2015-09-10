/* 		OBJECT-ORIENTED RECOGNIZER FOR SIMPLE EXPRESSIONS
 expr    -> term   (+ | -) expr | term
 term    -> factor (* | /) term | factor
 factor  -> number | real_number | '(' expr ')'     
 */

public class Parser {
	public static void main(String[] args) {
		System.out.println("Enter expression, end with semi-colon!\n");
		Lexer.lex();
		Expr e = new Expr();
		e.expr();
	}
}

class Expr { // expr -> term (+ | -) expr | term
	Term t;
	Expr e;
	char op;

	public void expr() {
		t = new Term();
		t.term();
		if (Lexer.nextToken == Token.ADD_OP || Lexer.nextToken == Token.SUB_OP) {
			op = Lexer.nextChar;
			Lexer.lex();
			e = new Expr();
			e.expr();
		}
	}
}

class Term { // term -> factor (* | /) term | factor
	Factor f;
	Term t;
	char op;

	public void term() {
		f = new Factor();
		f.factor();
		if (Lexer.nextToken == Token.MULT_OP || Lexer.nextToken == Token.DIV_OP) {
			op = Lexer.nextChar;
			Lexer.lex();
			t = new Term();
			t.term();
		}
	}
}

class Factor { // factor -> number | '(' expr ')'
	Expr e;
	int i;

	public void factor() {
		switch (Lexer.nextToken) {
		case Token.INT_LIT: // number
			i = Lexer.intValue;
			Lexer.lex();
			break;
		case Token.LEFT_PAREN: // '('
			Lexer.lex();
			e = new Expr();
			e.expr();
			Lexer.lex(); // skip over ')'
			break;
		default:
			break;
		}
	}
}
