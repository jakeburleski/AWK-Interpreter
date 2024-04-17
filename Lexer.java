import java.util.HashMap;

import java.util.LinkedList;
import java.util.Map;



/*
Notes for self:
fix decimal in processnumber
*/


public class Lexer {

	
	
	private StringHandler handle;     // StringHandler object
	
	private int lineNum=1;            // Holds what line number a character is on
	
	private int position=0;           // Holds what character position a character is in
	
	// Linked List of tokens
	LinkedList<Token> tokenValues = new LinkedList<Token>();
	
	
	private HashMap<String, Token.TokenType> keyWords = new HashMap<>();
	
	
	private HashMap<String, Token.TokenType> symbolsTwoChar = new HashMap<>();
	private HashMap<String, Token.TokenType> symbolsSingleChar = new HashMap<>();
	
	
	// Constructor
	public Lexer(String file) {
		
		
		// Creates StringHandler object
		handle = new StringHandler(file);
		
		
		
		// adds key and value pairs to keyWords HashMap
		keyWords.put("while", Token.TokenType.WHILE);
		keyWords.put("if", Token.TokenType.IF);
		keyWords.put("do", Token.TokenType.DO);
		keyWords.put("for", Token.TokenType.FOR);
		keyWords.put("break", Token.TokenType.BREAK);
		keyWords.put("continue", Token.TokenType.CONTINUE);
		keyWords.put("else", Token.TokenType.ELSE);
		keyWords.put("return", Token.TokenType.RETURN);
		keyWords.put("BEGIN", Token.TokenType.BEGIN);
		keyWords.put("END", Token.TokenType.END);
		keyWords.put("print", Token.TokenType.PRINT);
		keyWords.put("printf", Token.TokenType.PRINTF);
		keyWords.put("next", Token.TokenType.NEXT);
		keyWords.put("in", Token.TokenType.IN);
		keyWords.put("delete", Token.TokenType.DELETE);
		keyWords.put("getline", Token.TokenType.GETLINE);
		keyWords.put("exit", Token.TokenType.EXIT);
		keyWords.put("nextfile", Token.TokenType.NEXTFILE);
		keyWords.put("function", Token.TokenType.FUNCTION);
		
		
		
		// adds key and value pairs to symbolsTwoChar HashMap
		symbolsTwoChar.put(">=", Token.TokenType.GREATERTHANOREQUALS);
		symbolsTwoChar.put("++", Token.TokenType.PLUSPLUS);
		symbolsTwoChar.put("--", Token.TokenType.MINUSMINUS);
		symbolsTwoChar.put("<=", Token.TokenType.LESSTHANOREQUALS);
		symbolsTwoChar.put("==", Token.TokenType.EQUALSEQUALS);
		symbolsTwoChar.put("!=", Token.TokenType.NOTEQUALS);
		symbolsTwoChar.put("^=", Token.TokenType.EXPONENTEQUALS);
		symbolsTwoChar.put("%=", Token.TokenType.REMAINDEREQUALS);
		symbolsTwoChar.put("*=", Token.TokenType.MULTIPLYEQUALS);
		symbolsTwoChar.put("/=", Token.TokenType.DIVIDEEQUALS);
		symbolsTwoChar.put("+=", Token.TokenType.PLUSEQUALS);
		symbolsTwoChar.put("-=", Token.TokenType.MINUSEQUALS);
		symbolsTwoChar.put("!~", Token.TokenType.DOESNOTMATCH);
		symbolsTwoChar.put("&&", Token.TokenType.ANDAND);
		symbolsTwoChar.put(">>", Token.TokenType.APPEND);
		symbolsTwoChar.put("||", Token.TokenType.OR);
		
		
		// adds key and value pairs to symbolsSingleChar HashMap
		
		symbolsSingleChar.put("{", Token.TokenType.LEFTBRACE);
		symbolsSingleChar.put("}", Token.TokenType.RIGHTBRACE);
		symbolsSingleChar.put("[", Token.TokenType.LEFTBRACKET);
		symbolsSingleChar.put("]", Token.TokenType.RIGHTBRACKET);
		symbolsSingleChar.put("(", Token.TokenType.LEFTPARENTHESIS);
		symbolsSingleChar.put(")", Token.TokenType.RIGHTPARENTHESIS);
		symbolsSingleChar.put("$", Token.TokenType.SIGIL);
		symbolsSingleChar.put("~", Token.TokenType.MATCH);
		symbolsSingleChar.put("=", Token.TokenType.EQUALS);
		symbolsSingleChar.put("<", Token.TokenType.LESSTHAN);
		symbolsSingleChar.put(">", Token.TokenType.GREATERTHAN);
		symbolsSingleChar.put("!", Token.TokenType.NOT);
		symbolsSingleChar.put("+", Token.TokenType.PLUS);
		symbolsSingleChar.put("^", Token.TokenType.EXPONENT);
		symbolsSingleChar.put("-", Token.TokenType.MINUS);
		symbolsSingleChar.put("?", Token.TokenType.QUESTIONMARK);
		symbolsSingleChar.put(":", Token.TokenType.COLON);
		symbolsSingleChar.put("*", Token.TokenType.MULTIPLY);
		symbolsSingleChar.put("/", Token.TokenType.DIVIDE);
		symbolsSingleChar.put("%", Token.TokenType.REMAINDER);
		symbolsSingleChar.put(";", Token.TokenType.SEPERATOR);
		symbolsSingleChar.put("\n", Token.TokenType.SEPERATOR);
		symbolsSingleChar.put("|", Token.TokenType.PIPE);
		symbolsSingleChar.put(",", Token.TokenType.COMMA);
		
	}
	
	
	// Method that uses StringHandler to read through AWK file
	public LinkedList<Token> Lex() {
		
		
		
		
		
		
		
		// While there is still text left to read
		while (handle.IsDone() == false){
		
			
			
			
			
			
			
			// Looks for quotation marks in file, then calls method to create STRINGLITERAL token
			if (handle.Peek(0) == '"') {
				
				position++;
				handle.GetChar();
				
				tokenValues.add(HandleStringLiteral());
				
				
				
			}
			
			
			
			
			
			
			else if (handle.Peek(0) == '`') {
				
				position++;
				handle.GetChar();
				
				tokenValues.add(HandlePattern());
				
				
				
			}
			
			
			
			
			// if peek sees a comment starting
			else if (handle.Peek(0) == ('#')) {
				
				// loop through rest of line
				while(handle.Peek(0) != '\n' && handle.IsDone() == false ) {
					position++;
					handle.GetChar();
				}
				
				// go to next line
				lineNum++;
				
			}
			
			
			// if character is a space or tab, increment position
			else if (handle.Peek(0) == (' ') || handle.Peek(0) == '	') {
				
				position++;
				handle.GetChar();
			}
			
			
			// if character is \n, add a seperator token to Linked List 
			else if (handle.Peek(0) == ('\n')) {
				
				
				handle.GetChar();
				Token.TokenType sep = Token.TokenType.SEPERATOR;
				
				tokenValues.add(new Token(sep, lineNum, position));
				
				
				lineNum++;
				position=0;
				
				
			}
			
			
			// If a carriage character if seen, do nothing and move on
			else if (handle.Peek(0) == ('\r')) {
				handle.GetChar();
			}
			
			
			// If the character is a letter, call ProcessWord and add word to linked list
			else if (Character.isLowerCase(handle.Peek(0)) || Character.isUpperCase(handle.Peek(0)) ) {
				
				
				// changes char to String, Calls ProcessWord, then adds the WORD token to linked list
				tokenValues.add(ProcessWord(Character.toString(handle.GetChar())));
				
			}
			
			
			// If the character is a number, call ProcessNumber and add number to linked list
			else if (Character.isDigit(handle.Peek(0))) {
				
				// changes char to String, Calls ProcessNumber, then adds the NUMBER token to linked list
				tokenValues.add(ProcessNumber(Character.toString(handle.GetChar())));
				
			}
			
			
			// if a character is not a letter or a digit, check to see if it is a symbol
			else if (!(Character.isLowerCase(handle.Peek(0)) || Character.isUpperCase(handle.Peek(0)) || Character.isDigit(handle.Peek(0)))) {
				
				Token symbolHolder = ProcessSymbol();   // holds onto what will be checked is a symbol
				
				
				// if what ProcessSymbol returned is a symbol, add it to the linked list
				if(symbolHolder.type != Token.TokenType.NOTRECOGNIZED) {
					tokenValues.add(symbolHolder);
				}
				
				
				// If a character isn't recognized print error
				else {
					tokenValues.add(symbolHolder);
					break;
				}
				
				
				
			}
					
			
		
	}
		
		
		// Adds a Seperator to the end of the input
		tokenValues.add(new Token(Token.TokenType.SEPERATOR, lineNum, position));
		
		
		return tokenValues;
		
		
	}
	
	
	
	// processes word tokens
	private Token ProcessWord(String x) {
		
		String word = x;
		position++;
		
		// while recognizable letters are seen, create a word token
		while(Character.isLowerCase(handle.Peek(0)) || Character.isUpperCase(handle.Peek(0)) || Character.isDigit(handle.Peek(0)) || handle.Peek(0) == '_') {
			
			
			
			word+= handle.GetChar();
			position++;
			
			
			
		}
		
		
		
		// Creates a WORD TokenType
		Token.TokenType finalWord = Token.TokenType.WORD;
		
		
		// for each loop that iterates through keyWords HashMap
		for (Map.Entry<String, Token.TokenType> i : keyWords.entrySet()) {
		
			String key = i.getKey();    // Holds the value of the key
			
			
			// if word made in ProcessWord is a keyword, return keyword token
			if (word.equals(key)) {
				
				return new Token(i.getValue(), lineNum, position);
				
			}
			
			
			
		}
		
		return new Token(finalWord, lineNum, position, word);
		
	}
	
	
	
	// processes number tokens
	private Token ProcessNumber(String x) {
		
		String number = x;
		position++;
		
		// counter for decimals
		int decCounter = 0;
		
		
		while(Character.isDigit(handle.Peek(0)) || handle.Peek(0) == '.') {
			
			// counts how many decimals are in the string
			if (handle.Peek(0) == '.') {
				decCounter++;
			}
			
			// will break if a second decimal appears while reading
			if (decCounter > 1) {
			
				// fix this
				System.out.print("That number is not valid");
				break;
			}
			
		
			number+= handle.GetChar();
			position++;
			
		}
		
		// Creates a NUMBER TokenType
		 Token newToken = new Token(Token.TokenType.NUMBER, lineNum, position, number);
		 return newToken;
		 
		
	}
	
	// method to read regular expressions
	private Token HandlePattern() {
		
		// set string to empty
		String insideBackticks = "";
				
				
				
		// while the next character isn't a backtick
		while(handle.Peek(0) != '`') {
				
					
			// handles backslashes that are followed by a backtick inside a regular expression
			if(handle.Peek(0) == '\\') {
						
				position++;
				handle.GetChar();
						
				if(handle.Peek(0) == '`') {
					position++;
					insideBackticks += handle.GetChar();
							
					}
						
						
						
						
				}
							
					
			position++;
			insideBackticks += handle.GetChar();
					
			}
				
		position++;
		handle.GetChar();
				
		Token regExpressionToken = new Token(Token.TokenType.REGULAREXPRESSION, lineNum, position, insideBackticks);
				
				
		return regExpressionToken;
		
		
		
		
	}
	
	
	// method to read symbols
	private Token ProcessSymbol() {
		
		String twoSymbolHolder = "";
		String oneSymbolHolder = "";
		
		
		// if else statement used if file ends in single symbol, so hande doesn't peek out of bounds
		if(handle.Remainder().length()<2) {
			oneSymbolHolder = Character.toString(handle.Peek(0));                      // holds a symbol if it has one character
		}
		
		else {
		twoSymbolHolder = Character.toString(handle.Peek(0)) + Character.toString(handle.Peek(1));     // holds a symbol if it has two characters
		oneSymbolHolder = Character.toString(handle.Peek(0));                      // holds a symbol if it has one character
		}
		
		
		
		
		// for each loop that iterates through symbolsTwoChar HashMap
		for (Map.Entry<String, Token.TokenType> i : symbolsTwoChar.entrySet()) {
				
			String key = i.getKey();    // Holds the value of the key
					
					
			// if character found in iteration is a symbol, return symbol token
			if (twoSymbolHolder.equals(key)) {
						
				handle.GetChar();
				handle.GetChar();
				position+=2;   // adds 2 to position because the symbol is 2 characters
				
				return new Token(i.getValue(), lineNum, position);
						
				}
			
			
		}
		
		// for each loop that iterates through symbolsSingleChar HashMap
		for (Map.Entry<String, Token.TokenType> j : symbolsSingleChar.entrySet()) {
						
			String key = j.getKey();    // Holds the value of the key
			
			// if character found in iteration is a symbol, return symbol token
			if(oneSymbolHolder.equals(key)) {
				
				handle.GetChar();
				position++;   // adds 1 to position because symbol is 1 character
				
				return new Token(j.getValue(), lineNum, position);
				
				}
			
		}
			
					
			
		
			return new Token(Token.TokenType.NOTRECOGNIZED, lineNum, position);
		
		
		
		
	}
	
	
	
	
	// handles Strings
	private Token HandleStringLiteral() {
		
		// set string to empty
		String insideQuotes = "";
		
		
		
		// while the next character isn't a quotation marks
		while(handle.Peek(0) != '"') {
		
			
			// handles backslashes that are followed by a quotation mark inside a string
			if(handle.Peek(0) == '\\') {
				
				position++;
				handle.GetChar();
				
				if(handle.Peek(0) == '"') {
					position++;
					insideQuotes += handle.GetChar();
					
				}
				
				
				
				
			}
					
			
			
			position++;
			insideQuotes += handle.GetChar();
			
			
		}
		
		position++;
		handle.GetChar();
		
		Token quotesToken = new Token(Token.TokenType.STRINGLITERAL, lineNum, position, insideQuotes);
		
		
		return quotesToken;
		
		
		
		
	}
	
	
}
