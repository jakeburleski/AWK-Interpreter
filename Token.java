
public class Token {

	
	
	
	// Creates the different types of tokens
	enum TokenType {WORD, NUMBER, SEPERATOR, WHILE, IF, DO, FOR, BREAK, CONTINUE, ELSE, RETURN,
		BEGIN, END, PRINT, PRINTF, NEXT, IN, DELETE, GETLINE, EXIT, NEXTFILE, FUNCTION, STRINGLITERAL, REGULAREXPRESSION,
		GREATERTHANOREQUALS, PLUSPLUS, MINUSMINUS, LESSTHANOREQUALS, EQUALSEQUALS, NOTEQUALS, EXPONENTEQUALS, REMAINDEREQUALS,
		MULTIPLYEQUALS, DIVIDEEQUALS, PLUSEQUALS, MINUSEQUALS, DOESNOTMATCH, ANDAND, APPEND, OR, LEFTBRACE, RIGHTBRACE, LEFTBRACKET, RIGHTBRACKET, 
		LEFTPARENTHESIS, RIGHTPARENTHESIS, SIGIL, MATCH, EQUALS, LESSTHAN, GREATERTHAN, NOT, PLUS, EXPONENT, MINUS, QUESTIONMARK, COLON, MULTIPLY,
		DIVIDE, REMAINDER, SEMICOLON, NEWLINE, PIPE, COMMA, NOTRECOGNIZED};
	
	
	
	
	String tokenValue;  // the actual tokens string value is
	
	int lineNum;        // line number the current string is on
	
	int position;       // position of the character in terms of the current token its reading
	
	TokenType type;     // Differed token types a token can be labeled as
	
	
	// class constructor for new line
	public Token(TokenType type, int lineNum, int position) {
		
		this.type = type;
		
		this.lineNum = lineNum;
		
		this.position = position;
	}
	
	// class constructor for all other tokens with value
	public Token (TokenType type, int lineNum, int position, String value) {
		
		this.type = type;
		
		this.lineNum = lineNum;
		
		this.position = position;
		
		tokenValue = value;
		
		
	}
	
	
	// toString method override
	@SuppressWarnings("static-access")
	public String toString() {
		
		
		
		if(type == TokenType.WORD) {
			return "WORD>" + "(" + tokenValue + ")" ;
		}
		
		else if(type == TokenType.NUMBER) {
			return "NUMBER>" + "(" + tokenValue + ")" ;
		}
		
		else if(type == TokenType.SEPERATOR) {
			return "SEPERATOR";
		}
		
		
		else if (type == TokenType.STRINGLITERAL) {
			
			return "STRINGLITERAL>" + "(" + tokenValue + ")";
			
		}
		
		
		else if (type == TokenType.REGULAREXPRESSION) {
			
			return "REGULAREXPRESSION>" + "(" + tokenValue + ")";
			
		}
		
		else if (type == TokenType.NOTRECOGNIZED) {
			return "String not recognized";
		}
		
		
		
		else if (type != null) {
			
			return "" + type;
		}
		
		
		
		
		else {
			
			return "This message should never show. if it does, fix it stupid";
		}
		
		
	}
	
	
	
	
}
