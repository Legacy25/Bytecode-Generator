import java.io.*;

public class Lexer {

	static private char ch = ' ';
	static private char ident = ' ';
	static private Buffer buffer = new Buffer(new DataInputStream(System.in));
	static public int nextToken;
	static public char nextChar;
	static public int intValue;

	public static int lex() {
		while (Character.isWhitespace(ch))
			ch = buffer.getChar();
		if (Character.isLetter(ch)) {
			ident = Character.toLowerCase(ch);
			ch = buffer.getChar();
			nextToken = Token.ID;
		} else if (Character.isDigit(ch)) {
			nextToken = getNumToken(); // intValue would be set
		} else {
			nextChar = ch;
			switch (ch) {
			case ';':
				nextToken = Token.SEMICOLON;
				break;
			case ',':
				nextToken = Token.COMMA;
				break;
			case '+':
				nextToken = Token.ADD_OP;
				break;
			case '-':
				nextToken = Token.SUB_OP;
				break;
			case '*':
				nextToken = Token.MULT_OP;
				break;
			case '/':
				nextToken = Token.DIV_OP;
				break;
			case '=':
				nextToken = Token.ASSIGN_OP;
				break;
			case '(':
				nextToken = Token.LEFT_PAREN;
				break;
			case ')':
				nextToken = Token.RIGHT_PAREN;
				break;
			default:
				error("Illegal character " + ch);
				break;
			}
			ch = buffer.getChar();
		}
		return nextToken;
	} // lex

	private static int getNumToken() {
		int num = 0;
		do {
			num = num * 10 + Character.digit(ch, 10);
			ch = buffer.getChar();
		} while (Character.isDigit(ch));
		intValue = num;
		return Token.INT_LIT;
	}
	
	public int number() {
		return intValue;
	} // number

	public Character identifier() {
		return ident;
	} // letter

	public static void error(String msg) {
		System.err.println(msg);
		System.exit(1);
	} // error


}
