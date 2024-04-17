import java.util.LinkedList;
import java.util.Optional;

public class Parser {

	private TokenManager parserHandle; 		// TokenManager object
	
	// Parser class constructor
	public Parser(LinkedList<Token> tokenList) {
		
		
		parserHandle = new TokenManager(tokenList);
		
		
	}
	
	
	
	
	
	
	
	
	
	
	// accepts any number of seperators, returns true if there is at least one found
	public boolean AcceptSeperators() {
		
		// starts false to assume there are no SEPERATOR tokens
		Boolean sepReturn = false;
		
		
		
		// if the token is of TokenType SEPERATOR it will hold the value of said token
		Optional<Token> holdSepToken = parserHandle.MatchAndRemove(Token.TokenType.SEPERATOR);
		
		
		// will probably have to fix this while debugging bc if statements may not work
		// Loops through the LinkedList of tokens
		while(!holdSepToken.equals(Optional.empty())) {
			
			
			
			// if the token is of type SEPERATOR, change the boolean to true
			if(!holdSepToken.equals(Optional.empty())) {
				sepReturn = true;
			}
			
			
			
			holdSepToken = parserHandle.MatchAndRemove(Token.TokenType.SEPERATOR);
			
		}
		
		
		return sepReturn;
	}
	
	
	
	
	
	
	
	
	
	
	
	// method that does the parsing in class Parser
	public ProgramNode parse() { 
		
		// creates ProgramNode object containing the LinkedList of tokens from Lexer
		ProgramNode program = new ProgramNode(parserHandle.tokenList);
		
		
		// while the LinkedList of tokens previously mentioned is not empty and the LinkedList is not at the end of the file
		while(parserHandle.MoreTokens() && parserHandle.HowManyTokens() > 1) {
			
			// if multiple functions are in a file, this eats up the SEPERATOR tokens that are in between them
			while(!(parserHandle.MatchAndRemove(Token.TokenType.SEPERATOR).equals(Optional.empty())) && parserHandle.HowManyTokens() > 2) {
				parserHandle.MatchAndRemove(Token.TokenType.SEPERATOR);
			}
			
			// if end of input has seperators, breaks out of while loop so ParseFunction and ParseAction are not called
			if((parserHandle.HowManyTokens() <= 2 && (parserHandle.Peek(0).orElseThrow().type.equals(Token.TokenType.SEPERATOR)))) {
				
				// results in 2 SEPERATOR tokens left, gets rid of 1 of them
				parserHandle.MatchAndRemove(Token.TokenType.SEPERATOR);
				break;
			}
			
			
			// if program does not have correct grammar in the function or action, the program is not valid 
			if(ParseFunction(program) == false && ParseAction(program) == false) {
				
				System.out.print("Not valid program");
				break;
				
			}
			
			
			
			
			
		}
		
		return program;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Parses the function
	public boolean ParseFunction(ProgramNode program) {
		
		
		// checks to see if the first token is of type FUNCTION, if not then return false
		Optional<Token> holdFUNCTION = parserHandle.MatchAndRemove(Token.TokenType.FUNCTION);
		
		if(holdFUNCTION.equals(Optional.empty())) {
			return false;
		}
		
		
		
		// checks to see if the function name token is of type WORD, if not then return false
		Optional<Token> holdFuncName = parserHandle.MatchAndRemove(Token.TokenType.WORD);
		
		if(holdFuncName.equals(Optional.empty())) {
			return false;
		}
		
		// gets string of function name
		String functionNameToString = holdFuncName.orElseThrow().tokenValue;
		
		
		// checks to see if the next token is of type LEFTPARENTHESIS, if not then return false
		Optional<Token> holdLeftParen = parserHandle.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS);
		
		if(holdLeftParen.equals(Optional.empty())) {
			return false;
		}
		
		
		
		
		
		
		// checks to see if the parameters token is of type WORD, then loops through all of parameters
		Optional<Token> holdParam = parserHandle.MatchAndRemove(Token.TokenType.WORD);
		
		// Will hold parameter(s) to be sent to FunctionDefinitionNode
		LinkedList<Optional<Token>> paramList = new LinkedList<>();
		
		
		
		
		
		
		Optional<Token> holdRightParen = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
		
		
		if(holdRightParen.equals(Optional.empty())) {
		
			// while loop that is used to take in multiple parameters
			while(!holdParam.equals(Optional.empty()) || !holdRightParen.equals(Optional.empty())) {
				
				// adds parameters to LinkedList paramList
				paramList.add(holdParam);
				
				
				holdRightParen = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
				
				
				// breaks out of for loop when encountering the right parenthesis
				if(!holdRightParen.equals(Optional.empty())) {
					break;
				}
				
				
				
				
				
				// looks for SEPERATOR token
				holdParam = parserHandle.MatchAndRemove(Token.TokenType.SEPERATOR);
				
				// while loop that loops through SEPERATOR tokens until a comma is found
				while(!holdParam.equals(Optional.empty())) {
					holdParam = parserHandle.MatchAndRemove(Token.TokenType.SEPERATOR);
				}
				
				
				
				
				
				// looks for COMMA token
				holdParam = parserHandle.MatchAndRemove(Token.TokenType.COMMA);
				
				// returns false if there is not a comma following a parameter
				if(holdParam.equals(Optional.empty())) {
					return false;
				}
				
				
				// looks for SEPERATOR token
				holdParam = parserHandle.MatchAndRemove(Token.TokenType.SEPERATOR);
										
				// while loop that loops through SEPERATOR tokens until a comma is found
				while(!holdParam.equals(Optional.empty())) {
					holdParam = parserHandle.MatchAndRemove(Token.TokenType.SEPERATOR);
				}
				
				
				// checks for WORD token
				holdParam = parserHandle.MatchAndRemove(Token.TokenType.WORD);
				
				
				// skips over the commas between parameters
				if(holdParam.equals(Optional.empty())) {
					return false;
				}
				
				
			}
		}
		
		
		else {
			
			// adds parameters to LinkedList paramList
			paramList.add(holdParam);
		}
		
		
		
		// is calling for 
		BlockNode holdBlock = ParseBlock();
		
		
		// creates new function node and adds it to the LinkedList of functions nodes in class ProgramNode
		FunctionDefinitionNode newFunc = new FunctionDefinitionNode(functionNameToString, paramList, holdBlock);
		program.FUNCTION.add(newFunc);
		
		
		
		return true;
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// method to make sure action is valid
	public boolean ParseAction(ProgramNode program) {
		
		Optional<Token> holdBEGIN = parserHandle.MatchAndRemove(Token.TokenType.BEGIN);      //checks for BEGIN token
		Optional<Token> holdEND = parserHandle.MatchAndRemove(Token.TokenType.END);          //checks for END token  
		BlockNode holdBlock;   // BlockNode object
		
		
		// if the token is BEGIN, call ParseBlock() and add it to programs BEGIN LinkedList
		if(!holdBEGIN.equals(Optional.empty())) {
			
			holdBlock = ParseBlock();
			program.BEGIN.add(holdBlock);
		}
		
		
		
		
		// if the token is END, call ParseBlock() and add it to programs END LinkedList
		else if(!holdEND.equals(Optional.empty())) {
			
			holdBlock = ParseBlock();
			program.END.add(holdBlock);
		}
		
		
		// if the token is neither BEGIN or END, call ParseOperation(), then call ParseBlock() and add it to programs OTHER LinkedList
		else {
			Optional<Node> holdOptional = ParseOperation();
			
			if(holdOptional.equals(Optional.empty())) {
				return false;
			}
			
			holdBlock = ParseBlock();
			program.OTHER.add(holdBlock);
		}	
			return true;
	}
	
	
	
	Optional<Node> ParseBottomLevel(){
		
		
		
		
		// Optional<Token> to check TokenTypes
		Optional<Token> holdToken;
		
		// will call ParseOperation() ***were going to delete this later
		Optional<Node> parseOperationVar;
		
		
		// checks for STRINGLITERAL token
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.STRINGLITERAL);
		
		// if not empty
		if(!holdToken.equals(Optional.empty())) {
			
			Node strLitConstant = new ConstantNode(holdToken.orElseThrow().tokenValue);
			return Optional.of(strLitConstant);
		}
		
		
		
		
		
		
		// handles Patterns
		
		// check for REGULAREXPRESSION TokenType
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.REGULAREXPRESSION);
			
		// if not empty
		if(!holdToken.equals(Optional.empty())) {
					
			Node pattern = new PatternNode(holdToken.orElseThrow().tokenValue);
			return Optional.of(pattern);
		}
		
		
		
		
		
		
		
		
		// handles numbers
		
		// check for NUMBER TokenType
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.NUMBER);
				
		// if not empty
		if(!holdToken.equals(Optional.empty())) {
					
			Node numConstant = new ConstantNode(holdToken.orElseThrow().tokenValue);
			return Optional.of(numConstant);
			}
		
		
		
		
		// handles LPAREN ParseOperation() RPAREN
		
		// check for ( TokenType
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS);
		
		// if not empty
		if(!holdToken.equals(Optional.empty())) {
		
			// call ParseOperation
			parseOperationVar = ParseOperation();
			
			
			// if not empty
			if(!parseOperationVar.equals(Optional.empty())) {
				
				// check for ) TokenType
				holdToken = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
				
				if(!holdToken.equals(Optional.empty())) {
					
					
					
					return parseOperationVar;
				}	
			}
		}
		
		
		
		
		
		// handles NOT ParseOperation()
		
		// checks for NOT TokenType
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.NOT);
		
		// if not empty
		if(!holdToken.equals(Optional.empty())) {
			
			// call ParseOperation()
			parseOperationVar = ParseOperation();
			
			// if not empty
			if(!parseOperationVar.equals(Optional.empty())) {
				
				Node NOT_operation = new OperationNode(parseOperationVar.orElseThrow(), OperationNode.operations.NOT);
				
				return Optional.of(NOT_operation);
			}
		}
		
		
		
		
		
		
		// handles MINUS ParseOperation()
		
		// check for MINUS TokenType
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.MINUS);
		
		// if not empty
		if(!holdToken.equals(Optional.empty())) {
			
			// call ParseOperation()
			parseOperationVar = ParseOperation();
			
			// if not empty
			if(!parseOperationVar.equals(Optional.empty())) {
				
				Node MINUS_operation = new OperationNode(parseOperationVar.orElseThrow(), OperationNode.operations.UNARYNEG);
				
				return Optional.of(MINUS_operation);
			}
		}
		
		
		
		
		
		// handles PLUS ParseOperation()
		
		// check for PLUS TokenType
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.PLUS);
				
		// if not empty
		if(!holdToken.equals(Optional.empty())) {
			
			// call ParseOperation()
			parseOperationVar = ParseOperation();
			
			// if not empty
			if(!parseOperationVar.equals(Optional.empty())) {
				
				Node PLUS_operation = new OperationNode(parseOperationVar.orElseThrow(), OperationNode.operations.UNARYPOS);
				
				return Optional.of(PLUS_operation);
			}
		}
		
		
		
		
		// handles INCREMENT(++) ParseOperation()
		
		// check for PLUSPLUS TokenType
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.PLUSPLUS);
		
		// if not empty
		if(!holdToken.equals(Optional.empty())) {
			
			// call ParseOperation()
			parseOperationVar = ParseOperation();
			
			// if not empty
			if(!parseOperationVar.equals(Optional.empty())) {
				
				Node INCREMENT_operation = new OperationNode(parseOperationVar.orElseThrow(), OperationNode.operations.PREINC);
				return Optional.of(INCREMENT_operation);
			}
		}
		
		
		
		
		// handles DECREMENT(--) ParseOperation()
		
		// check for MINUSMINUS TokenType
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.MINUSMINUS);
				
		// if not empty
		if(!holdToken.equals(Optional.empty())) {
					
			// call ParseOperation()
			parseOperationVar = ParseOperation();
					
			// if not empty
			if(!parseOperationVar.equals(Optional.empty())) {
						
				Node DECREMENT_operation = new OperationNode(parseOperationVar.orElseThrow(), OperationNode.operations.PREDEC);
				return Optional.of(DECREMENT_operation);
			}
		}	
		
		
		
		// peeks to see if next token is a function token, and token after that is a parenthesis
		if(parserHandle.Peek(0).get().type.equals(Token.TokenType.WORD) && parserHandle.Peek(1).get().type.equals(Token.TokenType.LEFTPARENTHESIS)) {
			
			//call to ParseFunctionCall
			Optional<StatementNode> functionCall = ParseFunctionCall(Optional.empty());
			
			return Optional.of((Node)functionCall.orElseThrow());
			
		}
		
		
		// checks if next token is getline
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.GETLINE);
		if(!holdToken.equals(Optional.empty())) {
			
			//call to ParseFunctionCall
			Optional<StatementNode> getLine = ParseFunctionCall(Optional.of(Token.TokenType.GETLINE));
			
			return Optional.of((Node)getLine.orElseThrow());
		}
		
		

		// checks if next token is print
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.PRINT);
		if(!holdToken.equals(Optional.empty())) {
			
			//call to ParseFunctionCall
			Optional<StatementNode> print = ParseFunctionCall(Optional.of(Token.TokenType.PRINT));
			
			return Optional.of((Node)print.orElseThrow());
		}
		
		
		
		

		// checks if next token is printf
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.PRINTF);
		if(!holdToken.equals(Optional.empty())) {
			
			//call to ParseFunctionCall
			Optional<StatementNode> printf = ParseFunctionCall(Optional.of(Token.TokenType.PRINTF));
			
			return Optional.of((Node)printf.orElseThrow());
		}
		
		
		

		// checks if next token is exit
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.EXIT);
		if(!holdToken.equals(Optional.empty())) {
			
			//call to ParseFunctionCall
			Optional<StatementNode> exit = ParseFunctionCall(Optional.of(Token.TokenType.EXIT));
			
			return Optional.of((Node)exit.orElseThrow());
		}
		
		
		

		// checks if next token is nextfile
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.NEXTFILE);
		if(!holdToken.equals(Optional.empty())) {
			
			//call to ParseFunctionCall
			Optional<StatementNode> nextFile = ParseFunctionCall(Optional.of(Token.TokenType.NEXTFILE));
			
			return Optional.of((Node)nextFile.orElseThrow());
		}
		
		
		
		

		// checks if next token is next
		holdToken = parserHandle.MatchAndRemove(Token.TokenType.NEXT);
		if(!holdToken.equals(Optional.empty())) {
			
			//call to ParseFunctionCall
			Optional<StatementNode> next = ParseFunctionCall(Optional.of(Token.TokenType.NEXT));
			
			return Optional.of((Node)next.orElseThrow());
		}
		
		
		
		
		
		return ParseLValue();
		
		
	}
	
	
	
	
	
	
	
	Optional<Node> ParseLValue(){
		
		Optional<Token> hold;					// will hold any calls to MatchAndRemove			
		Optional<Node> bottomLevelVar;			// will hold any calls to method ParseBottomLevel()
		Optional<Node> parseOperationVar;		// will hold any calls to method ParseOperation()
		
		
		
		// handles references to fields (ex: $7)
		
		// checks for $ token
		hold = parserHandle.MatchAndRemove(Token.TokenType.SIGIL);
		
		// Checks for $ reference
		if(!hold.equals(Optional.empty())) {
			
			// call ParseBottomLevel()
			bottomLevelVar = ParseBottomLevel();
			
			// if not empty
			if(!bottomLevelVar.equals(Optional.empty())) {
			
				// changes bottomLevelVar from Optional<Node> to Node
				Node nonOptionalBottomLevel = bottomLevelVar.orElseThrow();
			
			
				Node operation = new OperationNode(nonOptionalBottomLevel, OperationNode.operations.DOLLAR);
				return Optional.of(operation);
			}
		}
		
		
		// checks for array references and variable references
		
		// looks for the array name
		Optional<Token> holdVarName = parserHandle.MatchAndRemove(Token.TokenType.WORD);
		
		// if not empty
		if(!holdVarName.equals(Optional.empty())) {
			
			// looks for open array bracket
			hold = parserHandle.MatchAndRemove(Token.TokenType.LEFTBRACKET);
			
			// if empty, continue for normal variable reference
			if(hold.equals(Optional.empty())) {
				
				Node normalVarRef = new VariableReferenceNode(holdVarName.orElseThrow().tokenValue);
				return Optional.of(normalVarRef);
				
			}
			
			
			
			
			// if not empty, continue for array reference
			else if(!hold.equals(Optional.empty())){
				
				// calls ParseOperation()
				parseOperationVar = ParseOperation();
				
				// if not empty
				if(!parseOperationVar.equals(Optional.empty())) {
					
					// looks for closing array bracket
					hold = parserHandle.MatchAndRemove(Token.TokenType.RIGHTBRACKET);
					
					// if not empty
					if(!hold.equals(Optional.empty())){
						
						// create variable reference node
						Node arrayVarRef = new VariableReferenceNode(holdVarName.orElseThrow().tokenValue, parseOperationVar); 
						
						return Optional.of(arrayVarRef);
					}
				}	
			}
		}
		
		
		
		
		// return empty if no previous conditions were met
		return Optional.empty();		
		
		
		
		
		
		
	}
	
	
	
	
	
	Optional<Node> ParsePostIncDec(){
		
		
		Optional<Node> bottomLevelVar = ParseBottomLevel();			// will hold any calls to method ParseBottomLevel()
		Optional<Token> hold;										// will hold any calls to MatchAndRemove			
		
		
		
		// if bottomLevelVar is not empty
		if(!bottomLevelVar.equals(Optional.empty())) {
		
			
			// if bottomLevelVar is an OperationNode
			if(bottomLevelVar.orElseThrow() instanceof OperationNode) {
			
				// Create new OperationNode by typecasting bottomLevelVar
				OperationNode postIncOrDec = (OperationNode)bottomLevelVar.orElseThrow();
			
				// if postIncOrDec is a post increment
				if(postIncOrDec.operation.equals(OperationNode.operations.POSTINC)) {
					
					return Optional.of(postIncOrDec);
					
				}
				
				// else if postIncOrDec is a post decrement
				else if(postIncOrDec.operation.equals(OperationNode.operations.POSTDEC)) {
					
					return Optional.of(postIncOrDec);
				}
			}
		}
		
		
		// if neither post increment or decrement, return the call to ParseBottomLevel()
		return bottomLevelVar;
	}
	
	
	
	
	
	
	Optional<Node> ParseExponents(){
		
		Optional<Node> left = ParsePostIncDec();				// calls ParsePostIncDec() for left node
		Optional<Token> hold = parserHandle.MatchAndRemove(Token.TokenType.EXPONENT);				// will hold any calls to MatchAndRemove			
		
		
		if(!left.equals(Optional.empty()) && !hold.equals(Optional.empty())) {
			
			
			Optional<Node> right = ParseExponents();	// recursively calls ParseExponents() for right node
			
			if(!right.equals(Optional.empty())) {
				
				OperationNode exponent = new OperationNode(left.orElseThrow(), right, OperationNode.operations.EXPONENT);
				return Optional.of(exponent);
			}
			
		}
		
		// returns empty if left is empty, or returns left by itself if there is no ^ symbol found
		return left;
		
		
	}
	
	
	
	
	
	
	
	Optional<Node> ParseFactor(){
		
		
		Optional<Node> left  = ParseExponents();			// will hold any calls to method ParseBottomLevel()
		
		
		
		// if bottomLevelVar is not empty
		if(!left.equals(Optional.empty())) {
			
			return left;
		}
		
			
			// also returns left, but will have value empty
			return left;
	}
	
	
	
	
	
	
	
	Optional<Node> ParseTerm(){
		
		Optional<Node> left = ParseFactor();								// holds left call to ParseFactor()
		Optional<Token> hold;												// will hold a call to MatchAndRemove			
		Optional<OperationNode.operations> operation = Optional.empty();	// holds operation from enum type in OperationNode
		
		
		
		
		
		
		
		// if the left doesn't come back as empty
		if(!left.equals(Optional.empty())) {
			
			
			// hold checks to see if next token is *
			hold = parserHandle.MatchAndRemove(Token.TokenType.MULTIPLY);
			
			
			
			
			// if its not * hold now checks to see if next token is /
			if(hold.equals(Optional.empty())) {
				
				hold = parserHandle.MatchAndRemove(Token.TokenType.DIVIDE);
			}
			
			
			// if it is a * operation is multiply
			else {
				operation = Optional.of(OperationNode.operations.MULTIPLY);
			}
			
			
			
				
			
			// if its not / hold now checks to see if next token is %
			if(hold.equals(Optional.empty())) {
					
				hold = parserHandle.MatchAndRemove(Token.TokenType.REMAINDER);
			}
			
			
			// if it is a / operation is now divide
			else {
				operation = Optional.of(OperationNode.operations.DIVIDE);
			}
			
			
			
			
			
			
			// if its not a % then hold is empty
			if(hold.equals(Optional.empty())) {
					hold = Optional.empty();
				}
			
		
			// if it is %, then operation becomes modulo
			else {
				operation = Optional.of(OperationNode.operations.MODULO);
			}
			
			
			if(hold.equals(Optional.empty())) {
				return left;
			}
			
			Optional<Node> right = ParseFactor();	// looks to see if there is a right factor
			Node term;								// holds term to be returned
			
			// if there is a right factor
			if(!right.equals(Optional.empty())) {
			
				term = new OperationNode(left.orElseThrow(), right, operation.orElseThrow());
				return Optional.of(term);
			}
			
			
			// if there isn't a right factor
			else {
				return Optional.empty();
			}
			
			
			
			
			
			
		}
		
		
		// returns empty from ParseFactor()'s empty
		return left;
		
		
		
	}
	
	
	
	
	
	
	
	Optional<Node> ParseExpression(){
		
		Optional<Node> left = ParseTerm();									// hold left term
		Optional<Token> hold;												// will hold a call to MatchAndRemove			
		Optional<OperationNode.operations> operation = Optional.empty();	// holds operation
		
		
		// if left term is not empty
		if(!left.equals(Optional.empty())) {
			
			// check to see if next token is a +
			hold = parserHandle.MatchAndRemove(Token.TokenType.PLUS);
			
			// if it isn't a +, check to see if it is a -
			if(hold.equals(Optional.empty())) {
				
				hold = parserHandle.MatchAndRemove(Token.TokenType.MINUS);
			}
			
			// if it is a +, operation is add
			else {
				operation = Optional.of(OperationNode.operations.ADD);
			}
			
			
			
			
			
			
			// if its not a - hold stays empty
			if(hold.equals(Optional.empty())) {
					hold = Optional.empty();
				}
			
			// if it is a -, operation becomes subtract
			else {
				operation = Optional.of(OperationNode.operations.SUBTRACT);
			}
			
			
			// returns call to ParseTerm() for left node if hold is empty
			if(hold.equals(Optional.empty())) {
				return left;
			}
			
			
			
			// checks to see if there is a right term
			Optional<Node> right = ParseTerm();
			
			// holds expression to be returned
			Node expression;
			
			
			// if there is a right term
			if(!right.equals(Optional.empty())) {
				
				expression = new OperationNode(left.orElseThrow(), right, operation.orElseThrow());
				return Optional.of(expression);
			}
			
			// if there isn't a right term
			else {
				return Optional.empty();
			}
			
		}
		
		
		// returns empty from ParseTerm()'s empty
		return left;
		
			
			
			
			
			
	}
		
		
		
	
	
	
	
	
	
	
	
	
	Optional<Node> ParseConcatenation(){
		
		
		Optional<Node> expression = ParseExpression();
		
		
		
		if(parserHandle.Peek(0).get().type.equals(Token.TokenType.STRINGLITERAL) || parserHandle.Peek(0).get().type.equals(Token.TokenType.WORD)) {
			
			Optional<Node> right = ParseExpression();
			
			
			Node concat;
			
			if(!right.equals(Optional.empty())) {
				
				concat = new OperationNode(expression.orElseThrow(),right,OperationNode.operations.CONCATENATION);	
				return Optional.of(concat);
			}
			
			
		}
		
		return expression;
	}
	
	
	
	
	
	
	Optional<Node> ParseBooleanCompare(){
		
		Optional<Node> concat = ParseConcatenation();						// call to ParseConcatenation
		Optional<Token> hold;												// will hold a call to MatchAndRemove
		Optional<OperationNode.operations> operation = Optional.empty();	// holds operation from enum type in OperationNode
		
		
		
		if(!concat.equals(Optional.empty())) {
			
			
			// hold checks to see if next token is <
			hold = parserHandle.MatchAndRemove(Token.TokenType.LESSTHAN);
								
								
								
								
			// if its not < hold now checks to see if next token is <=
			if(hold.equals(Optional.empty())) {
									
				hold = parserHandle.MatchAndRemove(Token.TokenType.LESSTHANOREQUALS);
			}
			
			// if it is < operation is LT
			else {
				operation = Optional.of(OperationNode.operations.LT);
			}
			
								
								
			// if its not <= hold now checks to see if next token is !=
			if(hold.equals(Optional.empty())) {
									
				hold = parserHandle.MatchAndRemove(Token.TokenType.NOTEQUALS);
			}
			
			
			// if it is <= operation is LE
			else {
				operation = Optional.of(OperationNode.operations.LE);
			}
			
			
			
								
						
			// if its not != hold now checks to see if next token is ==
			if(hold.equals(Optional.empty())) {
													
				hold = parserHandle.MatchAndRemove(Token.TokenType.EQUALSEQUALS);
			}
						
			

			// if it is != operation is NE
			else {
				operation = Optional.of(OperationNode.operations.NE);
			}
			
								
						
			// if its not == hold now checks to see if next token is >
			if(hold.equals(Optional.empty())) {
													
				hold = parserHandle.MatchAndRemove(Token.TokenType.GREATERTHAN);
			}
							
						
			

			// if it is == operation is EQ
			else {
				operation = Optional.of(OperationNode.operations.EQ);
			}

			
						
			// if its not > hold now checks to see if next token is >=
			if(hold.equals(Optional.empty())) {
													
				hold = parserHandle.MatchAndRemove(Token.TokenType.GREATERTHANOREQUALS);
				
				
				// if hold is >= operation is GE
				if(!hold.equals(Optional.empty())) {
					operation = Optional.of(OperationNode.operations.GE);
				}
			}
						
			
			// if it is > operation is GT
			else {
				operation = Optional.of(OperationNode.operations.GT);
			}
			
			
			
			
			
								
								
			// if hold is not empty
			if(!hold.equals(Optional.empty())) {
					
				
				Optional<Node> right = ParseConcatenation();
				
				Node bool;
				
				if(!right.equals(Optional.empty())) {
					
					bool = new OperationNode(concat.orElseThrow(), right, operation.orElseThrow());
					return Optional.of(bool);
				}
				
				else {
					return Optional.empty();
				}
				
				
			}
			return concat;
		}
		return Optional.empty();
	}
	
	
	
	
	
	
	
	Optional<Node> ParseMatch(){
		
		Optional<Node> bool = ParseBooleanCompare();						// call to ParseConcatenation
		Optional<Token> hold;												// will hold a call to MatchAndRemove
		Optional<OperationNode.operations> operation = Optional.empty();	// holds operation from enum type in OperationNode
		
		
		
		if(!bool.equals(Optional.empty())) {
			
			
			// hold checks to see if next token is ~
			hold = parserHandle.MatchAndRemove(Token.TokenType.MATCH);
								
								
								
								
			// if its not ~ hold now checks to see if next token is !~
			if(hold.equals(Optional.empty())) {
									
				hold = parserHandle.MatchAndRemove(Token.TokenType.DOESNOTMATCH);
				
				// if hold isn't empty, operation is notmatch
				if(!hold.equals(Optional.empty())) {
					operation = Optional.of(OperationNode.operations.NOTMATCH);
				}
					
			}
			
			// if it is ~ operation is match
			else {
				operation = Optional.of(OperationNode.operations.MATCH);
			}
			
			
			
		
			
			
			// if hold is not empty
			if(!hold.equals(Optional.empty())) {
								
							
				Optional<Node> right = ParseBooleanCompare();
							
				Node match;
							
				if(!right.equals(Optional.empty())) {
								
					match = new OperationNode(bool.orElseThrow(), right, operation.orElseThrow());
					return Optional.of(match);
				}
				
				else {
					return Optional.empty();
				}
					
					
			}
			return bool;
		}
		return Optional.empty();
			
		
	}
	
	
	
	
	
	
	Optional<Node> ParseArrayMembership(){
		
		Optional<Node> match = ParseMatch();	// holds calls to ParseMatch
		Optional<Token> hold;					// will hold a call to MatchAndRemove
		Node arrayMembership;					// will hold node to return
			
		
		
		// if match isn't empty
		if(!match.equals(Optional.empty())) {
			
			
		// hold checks to see if next token is in
		hold = parserHandle.MatchAndRemove(Token.TokenType.IN);
		
			
		// if there is an in
		if(!hold.equals(Optional.empty())) {
								
			// look for array name
			hold = parserHandle.MatchAndRemove(Token.TokenType.WORD);
				
		
			
			
			// if there is a word token (array name)
			if(!hold.equals(Optional.empty())) {
								
				arrayMembership = new OperationNode(match.orElseThrow(), OperationNode.operations.IN);
				return Optional.of(arrayMembership);
			}
			
			
					
			
		}
		
		
		return match;
		
		
		
	}
	return Optional.empty();
		
		
		
	}
	
	
	
	
	
	
	Optional<Node> ParseAND(){
		
		Optional<Node> arrayMembership = ParseArrayMembership();	// holds calls to ParseArrayMembership
		Optional<Token> hold;										// will hold a call to MatchAndRemove
		Node and;													// will hold node to return
		
		
		// if arrayMembership is not empty
		if(!arrayMembership.equals(Optional.empty())) {
			
			// checks for &&
			hold = parserHandle.MatchAndRemove(Token.TokenType.ANDAND);
			
			// if there is an &&
			if(!hold.equals(Optional.empty())) {
			
				Optional<Node> right = ParseArrayMembership();
				
				// if right is not empty
				if(!right.equals(Optional.empty())) {
					
					and = new OperationNode(arrayMembership.orElseThrow(),right,OperationNode.operations.AND);
					return Optional.of(and);
				}
				
				else {
					return Optional.empty();
				}
				
				
			}
			// if && doesn't exist, return arrayMembership
			return arrayMembership;
			
		}
		// if original call to ParseArrayMembership is empty, return empty
		return Optional.empty();
		
		
		
		
		
	}
	
	
	
	
	
	
	Optional<Node> ParseOr(){
		
		Optional<Node> and = ParseAND();		// holds calls to ParseAND
		Optional<Token> hold;					// will hold a call to MatchAndRemove
		Node or;								// will hold node to return
		
		
		// if and is not empty
		if(!and.equals(Optional.empty())) {
					
			// checks for ||
			hold = parserHandle.MatchAndRemove(Token.TokenType.OR);
					
			// if there is an ||
			if(!hold.equals(Optional.empty())) {
					
				Optional<Node> right = ParseAND();
						
				// if right is not empty
				if(!right.equals(Optional.empty())) {
					
					or = new OperationNode(and.orElseThrow(),right,OperationNode.operations.AND);
					return Optional.of(or);
				}
				
				else {
					return Optional.empty();
				}
				
						
			}
			// if || doesn't exist, return and
			return and;
					
		}
		// if original call to ParseAND is empty, return empty
		return Optional.empty();
		
		
		
		
	}
	
	
	
	
	
	
	Optional<Node> ParseTernary(){
		
		
		Optional<Node> and = ParseOr();							// holds calls to ParseOr
		Optional<Token> hold = Optional.empty();				// will hold a call to MatchAndRemove
		Node ternary;											// will hold node to return
		Optional<Node> trueResult = Optional.empty(); 			// holds true case which will be a call to Parse()
		
		// if and is not empty
		if(!and.equals(Optional.empty())) {
					
			// checks for ?
			hold = parserHandle.MatchAndRemove(Token.TokenType.QUESTIONMARK);
			
			if(!hold.equals(Optional.empty())) {
				
				// gets the true case
				trueResult = ParseOr();
				
				// if the true case is not empty
				if(!trueResult.equals(Optional.empty())) {
						
					// check for a :
					hold = parserHandle.MatchAndRemove(Token.TokenType.COLON);
				}
				
				// return empty if no colon
				else {
					return Optional.empty();
				}
					
			}
			
		}
		
		
		
		
			
		// if hold is not empty
		if(!hold.equals(Optional.empty())) {
					
				// recursively call ParseTernary() for false result
				Optional<Node> falseResult = ParseTernary();
				
				// if the false result returns as empty, then return empty
				if(falseResult.equals(Optional.empty())) {
					return Optional.empty();
				}
				
					
				ternary = new TernaryNode(and.orElseThrow(), trueResult.orElseThrow(), falseResult.orElseThrow());
					
				return Optional.of(ternary);
			}
			
			
		
			return and;
			
			
		
		
	}
	
	
	
	
	
	
	
	Optional<Node> ParseAssignment(){
		
		Optional<Node> target;														// will hold a ternary call to ParseTernary		
		Optional<Token> hold;														// will hold a call to MatchAndRemove			
		Optional<Node> bottomLevelVar;												// will hold any calls to method ParseBottomLevel()
		Optional<OperationNode.operations> holdOperation = Optional.empty();		// will be used to hold what operation is being perfromed, the second parameter for AssignmentNode
		
		
		target = ParseTernary();
		
		// if the left doesn't come back as empty
		if(!target.equals(Optional.empty())) {
					
					
			// hold checks to see if next token is ^=
			hold = parserHandle.MatchAndRemove(Token.TokenType.EXPONENTEQUALS);
					
					
					
					
			// if its not ^= hold now checks to see if next token is %=
			if(hold.equals(Optional.empty())) {
						
				hold = parserHandle.MatchAndRemove(Token.TokenType.REMAINDEREQUALS);
			}
			
			
			// if hold does find a ^=, make held operation ^
			else {
				holdOperation = Optional.of(OperationNode.operations.EXPONENT);
			}
			
			
					
					
			// if its not %= hold now checks to see if next token is *=
			if(hold.equals(Optional.empty())) {
							
				hold = parserHandle.MatchAndRemove(Token.TokenType.MULTIPLYEQUALS);
			}
			
			
			
			// if hold does find a %=, make held operation %
			else {
				holdOperation = Optional.of(OperationNode.operations.MODULO);
			}
			
			
					
			
			// if its not *= hold now checks to see if next token is /=
			if(hold.equals(Optional.empty())) {
										
				hold = parserHandle.MatchAndRemove(Token.TokenType.DIVIDEEQUALS);
			}
			
			
			// if hold does find a *=, make held operation *
			else {
				holdOperation = Optional.of(OperationNode.operations.MULTIPLY);
			}
			
			
			
					
			
			// if its not /= hold now checks to see if next token is +=
			if(hold.equals(Optional.empty())) {
										
				hold = parserHandle.MatchAndRemove(Token.TokenType.PLUSEQUALS);
			}
			
			
			
			// if hold does find a /=, make held operation /
			else {
				holdOperation = Optional.of(OperationNode.operations.DIVIDE);
			}
			
					
			
			
			// if its not += hold now checks to see if next token is -=
			if(hold.equals(Optional.empty())) {
										
				hold = parserHandle.MatchAndRemove(Token.TokenType.MINUSEQUALS);
			}
			
			
			// if hold does find a +=, make held operation +
			else {
				holdOperation = Optional.of(OperationNode.operations.ADD);
			}
			
			
			
			
			// if its not -= hold now checks to see if next token is =
			if(hold.equals(Optional.empty())) {
										
				hold = parserHandle.MatchAndRemove(Token.TokenType.EQUALS);
				
				// if hold does find a =, make held operation =
				if(!hold.equals(Optional.empty())) {
					
					holdOperation = Optional.of(OperationNode.operations.EQ);
				}
				
			}
			
			
			// if hold does find a -=, make held operation -
			else {
				holdOperation = Optional.of(OperationNode.operations.SUBTRACT);
			}
			
			
			
					
			// if hold is not empty
			if(!hold.equals(Optional.empty())) {
				
				
				
				
				
					
				Optional<Node> right = ParseAssignment();
				
				// if right returns as empty, then return empty
				if(right.equals(Optional.empty())) {
					return Optional.empty();
				}
				
				
				OperationNode operationOfAssignment = new OperationNode(target.orElseThrow(), right, holdOperation.orElseThrow());
				
				AssignmentNode returnAssignment = new AssignmentNode(target.orElseThrow(), operationOfAssignment);
				
			}
			
				
				
		}
		
		// returns either ParseTernary() or empty if ParseTernary() returned empty
		return target;
			
		
	}
	
	
	
	
	// Creates BlockNode object
	public BlockNode ParseBlock() {
		
		LinkedList<StatementNode> statementList = new LinkedList<>();						// holds linked list of statements
		Optional<Token> hold = parserHandle.MatchAndRemove(Token.TokenType.LEFTBRACE);		// checks for a left brace
		
		// for one line of block
		if(hold.equals(Optional.empty())) {
			statementList.add(ParseStatement().orElseThrow());
		}
		
		// for multiples lines of block
		else {
			Optional<StatementNode> statements = ParseStatement();
			
			hold = parserHandle.MatchAndRemove(Token.TokenType.RIGHTBRACE);		// checks for a right brace
			
			if(!statements.equals(Optional.empty())) {

				statementList.add(statements.orElseThrow());
				
				
			}
			
			
			
			// while there is no right brace
			while(hold.equals(Optional.empty())) {
				
				statements = ParseStatement();
				statementList.add(statements.orElseThrow());
				
				
				hold = parserHandle.MatchAndRemove(Token.TokenType.RIGHTBRACE);		// checks for a right brace
				
				
			}
			
		}
		
		return new BlockNode(statementList, Optional.empty());
		
	}
	
	
	
	
	
	// Returns Optional.empty object
	public Optional<Node> ParseOperation(){
		
		Optional<Node> holdNode = ParseAssignment();
		
		
		
		
	
		return holdNode;
		
		
	}
	
	
	
	
	public Optional<StatementNode> ParseStatement() {
		
	
		

		// checks if statement is a continue
		Optional<Token> checkContinue = parserHandle.MatchAndRemove(Token.TokenType.CONTINUE);
		if(!checkContinue.equals(Optional.empty())) {
			
			Optional<StatementNode> continueNode = ParseContinue();
			return continueNode;
			
		}
		
		
		

		// checks if statement is a break
		
		Optional<Token> checkBreak = parserHandle.MatchAndRemove(Token.TokenType.BREAK);
		if(!checkBreak.equals(Optional.empty())) {
			
			
			Optional<StatementNode> breakNode = ParseBreak();
			return breakNode;
		}
		
		
		
		// checks if statement is an if
		
		Optional<Token> checkIf = parserHandle.MatchAndRemove(Token.TokenType.IF);
		if(!checkIf.equals(Optional.empty())) {
					
					
			Optional<StatementNode> ifNode = ParseIf();
			return ifNode;
		}
				
		
		
		
		
		
		// checks if statement is a for
		Optional<Token> checkFor = parserHandle.MatchAndRemove(Token.TokenType.FOR);
		if(!checkFor.equals(Optional.empty())) {
			
			
			Optional<StatementNode> forNode = ParseFor();
			return forNode;
		}
		
		

		// checks if statement is a delete
		Optional<Token> checkDelete = parserHandle.MatchAndRemove(Token.TokenType.DELETE);
		if(!checkDelete.equals(Optional.empty())) {
			
			Optional<StatementNode> deleteNode = ParseDelete();
			return deleteNode;
			
		}
		
		
		

		// checks if statement is a while
		Optional<Token> checkWhile = parserHandle.MatchAndRemove(Token.TokenType.WHILE);
		if(!checkWhile.equals(Optional.empty())) {
			
			Optional<StatementNode> whileNode = ParseWhile();
			return whileNode;
			
		}
		
		

		// checks if statement is a do-while
		Optional<Token> checkDo = parserHandle.MatchAndRemove(Token.TokenType.DO);
		if(!checkDo.equals(Optional.empty())) {
			
			if(!checkDo.equals(Optional.empty())) {
			
				Optional<StatementNode> doWhileNode = ParseDoWhile();
				return doWhileNode;
			}
			
		}
		
		
		

		// checks if statement is a return
		Optional<Token> checkReturn = parserHandle.MatchAndRemove(Token.TokenType.RETURN);
		if(!checkReturn.equals(Optional.empty())) {
			
			Optional<StatementNode> returnNode = ParseReturn();
			return returnNode;
			
		}
		
		

		// checks if statement is a parseOperation()
		Optional<Node> checkParseOp = ParseOperation();
		
		/*
		// checks to see is ParseOperation() is an assignment or operation node or function node
		if(checkParseOp.get() instanceof AssignmentNode || checkParseOp.get() instanceof OperationNode || checkParseOp.get() instanceof FunctionDefinitionNode) {
		
			
			// checks if the call to ParseOperation() is an operation node, checks if its right node is empty, if so then return call to ParseOperation as a statement node
			if(checkParseOp.get() instanceof OperationNode) {
				
				
				OperationNode OperationParseOp = (OperationNode) checkParseOp.get();
				
				if(OperationParseOp.right.equals(Optional.empty())) {
					
					StatementNode parseOpNode = new ParseOperationNode(checkParseOp.orElseThrow());
					return Optional.of(parseOpNode);
					
				}
			}
			}
			*/	
				
			
		// if checkParseOp isn't empty, create a ParseOperation statement node
		if(!checkParseOp.equals(Optional.empty())) {
				
			StatementNode parseOpNode = new ParseOperationNode(checkParseOp.orElseThrow());
			return Optional.of(parseOpNode);
		}
			
			
			
		
		
		
		
		return Optional.empty();
		
		
		
		
		
		
		
	}
	
	
	public Optional<StatementNode> ParseContinue(){
		
		
		// creates and returns new continue node
		ContinueNode holdContinue = new ContinueNode();
		return Optional.of(holdContinue);
		
		
		
		
	}
	
	
	
	public Optional<StatementNode> ParseBreak(){
		
		
		
		// creates and returns new break node	
		BreakNode holdBreak = new BreakNode();
		return Optional.of(holdBreak);
		
		
		
		
	}
	
	
	
	public Optional<StatementNode> ParseIf() {
		
		// eats all seperators
		AcceptSeperators();
		
		Optional<Token> checkForIf;										// holds calls to matchandremove
		Optional<StatementNode> elseIfNode = Optional.empty();			// will hold recursives calls to ParseIf if there are else if blocks
		
		
		
		
		checkForIf = parserHandle.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS);
		
		// if an there is no ( token, return empty
		if(checkForIf.equals(Optional.empty())) {
			return Optional.empty();
		}
		

		// eats all seperators
		AcceptSeperators();
		
		
		
		
		
		
		
		
		Optional<Node> condition = ParseOperation();

		// if an there is no condition, return empty
		if(condition.equals(Optional.empty())) {
			return Optional.empty();
		}
		

		// eats all seperators
		AcceptSeperators();
		
		
		
		// checks for right parenthesis
		checkForIf = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
		
		// if an there is no ) token, return empty
		if(checkForIf.equals(Optional.empty())) {
			return Optional.empty();
		}
		

		// eats all seperators
		AcceptSeperators();
		
		
		
		// gets the statements
		BlockNode statements = ParseBlock();

		
		// eats all seperators
		AcceptSeperators();
		
		
		
		
		// checks if there is an else token
		checkForIf = parserHandle.MatchAndRemove(Token.TokenType.ELSE);
		
		

		// eats all seperators
		AcceptSeperators();
		
		
		// if there is an else token, 
		if(!checkForIf.equals(Optional.empty())) {
			
			
			checkForIf = parserHandle.MatchAndRemove(Token.TokenType.IF);
			
			// if there is an else if
			if(!checkForIf.equals(Optional.empty())) {
			
				elseIfNode = ParseIf();
				
				
				
			}
			
			
			// if its just an else
			else {
			
				statements = ParseBlock();
				
			}
			
			
			
			return Optional.of(new IfNode(condition.orElseThrow(), statements, elseIfNode));
			
			
		}
		
		
		
		else {
			return Optional.of(new IfNode(condition.orElseThrow(), statements, Optional.empty()));
		}
		
		
						
						
		
		
		
	}
	
	
	
	public Optional<StatementNode> ParseFor() {
		
		// eats all seperators
		AcceptSeperators();
		
		Optional<Token> checkForFor;				// holds calls to matchandremove
			
		
		// checks for left parenthesis
		checkForFor = parserHandle.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS);
				
		// if an there is no ( token, return empty
		if(checkForFor.equals(Optional.empty())) {
			return Optional.empty();
		}
				

		// eats all seperators
		AcceptSeperators();
		
		
		
		Optional<Node> initialVar = ParseOperation();
		

		// eats all seperators
		AcceptSeperators();
		
		
		
		// if there is no initialize variable int i = 0 or loop variable a, return empty
		if(initialVar.equals(Optional.empty())) {
			return Optional.empty();
		}
		
		if(parserHandle.Peek(0).orElseThrow().type.equals(Token.TokenType.IN)) {
			
			parserHandle.MatchAndRemove(Token.TokenType.IN);
			
			

			// eats all seperators
			AcceptSeperators();
			
			
			
			Optional<Node> array = ParseOperation();
			
			
			

			// eats all seperators
			AcceptSeperators();
			
			
			// if there is no name for what foreach is looping through, return empty
			if(array.equals(Optional.empty())) {
				return Optional.empty();
			}
			
			
			
			// checks for right parenthesis
			checkForFor = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
					
			// if an there is no ) token, return empty
			if(checkForFor.equals(Optional.empty())) {
				return Optional.empty();
			}
			
			
			
			// gets the body of the foreach loop
			BlockNode body = ParseBlock();
			
			
			StatementNode holdForEachNode = new ForEachNode(initialVar.orElseThrow(), array.orElseThrow(), body);
			return Optional.of(holdForEachNode);
			
			
			
		}
		
		
		else {
			
			
			// checks for semicolon
			checkForFor = parserHandle.MatchAndRemove(Token.TokenType.SEMICOLON);
								
			// if an there is no ; token, return empty
			if(checkForFor.equals(Optional.empty())) {
				return Optional.empty();
			}
			
			// eats all seperators
			AcceptSeperators();
			
			
			
			// gets the for loops condition
			Optional<Node> condition = ParseOperation();
			
			
			if(condition.equals(Optional.empty())) {
				return Optional.empty();
			}
			

			// eats all seperators
			AcceptSeperators();
			
			

			// checks for semicolon
			checkForFor = parserHandle.MatchAndRemove(Token.TokenType.SEMICOLON);
								
			// if an there is no ; token, return empty
			if(checkForFor.equals(Optional.empty())) {
				return Optional.empty();
			}
			
			// eats all seperators
			AcceptSeperators();
			
			
			
			// gets the for loops iteration
			Optional<Node> iteration = ParseOperation();
			
			
			if(iteration.equals(Optional.empty())) {
				return Optional.empty();
			}
			

			// eats all seperators
			AcceptSeperators();
			
			

			
			
			// checks for right parenthesis
			checkForFor = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
			
			// if an there is no ) token, return empty
			if(checkForFor.equals(Optional.empty())) {
				return Optional.empty();
			}
			

			// eats all seperators
			AcceptSeperators();
			
			
			
			
			// gets the body of the for loop
			BlockNode body = ParseBlock();
						
						
			StatementNode holdForEachNode = new ForNode(initialVar.orElseThrow(), condition.orElseThrow(), iteration.orElseThrow(), body);
			return Optional.of(holdForEachNode);
				
			
		}
		
		
		
	}
	
	
	public Optional<StatementNode> ParseDelete() {
		
		
		// gets either the array or array reference
		Optional<Node> whatToDelete = ParseLValue();
		
		// returns empty is call to ParseLValue is empty
		if(whatToDelete.equals(Optional.empty())) {
			return Optional.empty();
		}
		
		
		StatementNode holdDeleteNode = new DeleteNode(whatToDelete.orElseThrow());
		
		return Optional.of(holdDeleteNode);
		
		
		
		
	}
	
	
	public Optional<StatementNode> ParseWhile() {
		
		
		Optional<Token> checkForWhile;		// will hold calls to matchandremove

		// eats all seperators
		AcceptSeperators();
		
		
		

		// checks for left parenthesis
		checkForWhile = parserHandle.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS);
				
		// if an there is no ( token, return empty
		if(checkForWhile.equals(Optional.empty())) {
			return Optional.empty();
		}
				

		// eats all seperators
		AcceptSeperators();
		
		
		
		
		// gets while loops condition
		Optional<Node> condition = ParseOperation();
		
		
		
		
		if(condition.equals(Optional.empty())) {
			return Optional.empty();
		}
		
		// eats all seperators
		AcceptSeperators();
				

		
		
		// checks for right parenthesis
		checkForWhile = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
				
		// if an there is no ) token, return empty
		if(checkForWhile.equals(Optional.empty())) {
			return Optional.empty();
		}
				

		// eats all seperators
		AcceptSeperators();
		
		
		

		// call to ParseBlock() to get statements
		BlockNode statements = ParseBlock();
		
		
		StatementNode holdWhileNode = new WhileNode(condition.orElseThrow(), statements);
		
		return Optional.of(holdWhileNode);
		
		
		
		
		
	}
	
	
	public Optional<StatementNode> ParseDoWhile() {
		

		Optional<Token> checkForWhile;		// will hold calls to matchandremove

		// eats all seperators
		AcceptSeperators();
		
		
		

		// call to ParseBlock() to get statements
		BlockNode statements = ParseBlock();
		
		

		// eats all seperators
		AcceptSeperators();
	

		// checks for while
		checkForWhile = parserHandle.MatchAndRemove(Token.TokenType.WHILE);
		
		// if an there is no while token, return empty
		if(checkForWhile.equals(Optional.empty())) {
			return Optional.empty();
		}	
		
		
		

		// eats all seperators
		AcceptSeperators();
	
		

		// checks for left parenthesis
		checkForWhile = parserHandle.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS);
				
		// if an there is no ( token, return empty
		if(checkForWhile.equals(Optional.empty())) {
			return Optional.empty();
		}
				

		// eats all seperators
		AcceptSeperators();
		
		
		
		
		// gets do-while loops condition
		Optional<Node> condition = ParseOperation();
		
		
		
		
		if(condition.equals(Optional.empty())) {
			return Optional.empty();
		}
		
		// eats all seperators
		AcceptSeperators();
				

		
		
		// checks for right parenthesis
		checkForWhile = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
		
		
		StatementNode holdDoWhileNode = new DoWhileNode(condition.orElseThrow(), statements);
		
		return Optional.of(holdDoWhileNode);
		
		
	}
	
	
	public Optional<StatementNode> ParseReturn() {
		
		
		Optional<Node> lookForReturn = ParseOperation();
		
		
		
		
		StatementNode holdReturnNode = new ReturnNode(lookForReturn);
		
		
		return Optional.of(holdReturnNode);
		
		
	}
	
	

	public Optional<StatementNode> ParseFunctionCall(Optional<Token.TokenType> type) {
		

		// eats all seperators
		AcceptSeperators();
	
		// if function call is a normal function
		if(type.equals(Optional.empty())) {
		
			Optional<Token> functionName = parserHandle.MatchAndRemove(Token.TokenType.WORD);			// checks for function name
			LinkedList<Node> paramList = new LinkedList<>();											// linked list for parameters
			
			
			
			// if function name gets empty, return empty
			if(functionName.equals(Optional.empty())) {
				return Optional.empty();
			}
	
			// eats all seperators
			AcceptSeperators();
		
			
			Optional<Token> functionCheck;				// will hold calls to matchandremove
			
			
	
			// checks for left parenthesis
			functionCheck = parserHandle.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS);
					
			// if an there is no ( token, return empty
			if(functionCheck.equals(Optional.empty())) {
				return Optional.empty();
			}
					
	
			// eats all seperators
			AcceptSeperators();
			
			
			// call to get parameter reference
			Optional<Node> parameter = ParseOperation();
			
			// returns empty if there are no parameters
			if(parameter.equals(Optional.empty())) {
				return Optional.empty();
			}
			
			
			// adds parameter to linked list
			paramList.add(parameter.orElseThrow());
			
			
			
			// eats all seperators
			AcceptSeperators();
			
			
			// checks for a right parenthesis
			Optional<Token> checkRightParen = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
			

			// eats all seperators
			AcceptSeperators();
			
			
			// while a right parenthesis is not found
			while(!checkRightParen.equals(Optional.empty())) {
				
				
				
				
				
				
				// eats up commas
				functionCheck = parserHandle.MatchAndRemove(Token.TokenType.COMMA);
				
				
				if(functionCheck.equals(Optional.empty())) {
					return Optional.empty();
				}
	
				// eats all seperators
				AcceptSeperators();
				
				
				
				parameter = ParseOperation();
				
	
				// adds parameter to linked list
				paramList.add(parameter.orElseThrow());
				

				// eats all seperators
				AcceptSeperators();
				
				
				// checks for a right parenthesis
				checkRightParen = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
				
	
				// eats all seperators
				AcceptSeperators();
				
				
			}
			
			
	
					
			// if an there is no ) token, return empty
			if(functionCheck.equals(Optional.empty())) {
				return Optional.empty();
			}
				
			// gets String name from token
			String functionNameStr = functionName.orElseThrow().tokenValue;
			
			StatementNode holdFunctionCallNode = new FunctionCallNode(functionNameStr, paramList);
			
			return Optional.of(holdFunctionCallNode);
			
			
		}
		
		
		// for all function calls that don't need parenthesis
		else {
			
			
			LinkedList<Node> paramList = new LinkedList<>();
			
			
			// eats all seperators
			AcceptSeperators();
					
						
			Optional<Token> functionCheck;				// will hold calls to matchandremove
						
						
				
			//  clears optional left parenthesis
			functionCheck = parserHandle.MatchAndRemove(Token.TokenType.LEFTPARENTHESIS);
			
								
				
			// eats all seperators
			AcceptSeperators();
						
						
			// call to get parameter reference
			Optional<Node> parameter = ParseOperation();
			
			// returns empty if there are no parameters
			if(parameter.equals(Optional.empty())) {
				return Optional.empty();
			}
						
						
			// adds parameter to linked list
			paramList.add(parameter.orElseThrow());
						
						
						
			// eats all seperators
			AcceptSeperators();

			// checks for a right parenthesis
			Optional<Token> checkRightParen = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
			

			// eats all seperators
			AcceptSeperators();
			
			
			// while a right parenthesis is not found
			while(!checkRightParen.equals(Optional.empty())) {
				
				
				
				
				
				
				// eats up commas
				functionCheck = parserHandle.MatchAndRemove(Token.TokenType.COMMA);
				
				
				if(functionCheck.equals(Optional.empty())) {
					return Optional.empty();
				}
	
				// eats all seperators
				AcceptSeperators();
				
				
				
				parameter = ParseOperation();
				
	
				// adds parameter to linked list
				paramList.add(parameter.orElseThrow());
				

				// eats all seperators
				AcceptSeperators();
				
				
				// checks for a right parenthesis
				checkRightParen = parserHandle.MatchAndRemove(Token.TokenType.RIGHTPARENTHESIS);
				
	
				// eats all seperators
				AcceptSeperators();
				
				
			}		
											
			// if an there is no ) token, return empty
			if(functionCheck.equals(Optional.empty())) {
				return Optional.empty();
			}
							
						
			StatementNode noParenFunction = new FunctionCallNode(type.toString(), paramList);
						
			return Optional.of(noParenFunction);
			
			
			
			
			
			
			
		}
		
		
		
		
	}
	
	

	
	
	
	
	
	
	
	
	
}
