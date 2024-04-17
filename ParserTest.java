import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class ParserTest {

	
	
	
	
	@Test
	void generalAwkTest() {
		//fail("Not yet implemented");
		
		Lexer obj = new Lexer("hello_34\n 53<=100; 78");
		LinkedList<Token> myTokens = obj.Lex();
				
		
		
		// tests size of LinkedList is correct
		assertEquals(8, myTokens.size());
		
	
		
	
		//hello_34 should be in cell 0
		assertEquals("hello_34", myTokens.get(0).tokenValue);
		//tokentype should be word
		assertEquals(Token.TokenType.WORD, myTokens.get(0).type);
	
	
		//null should be in cell 1
		assertEquals(null, myTokens.get(1).tokenValue);
		//tokentype should be seperator
		assertEquals(Token.TokenType.SEPERATOR, myTokens.get(1).type);
		
	
		
		//53 should be in cell 2
		assertEquals("53", myTokens.get(2).tokenValue);
		//tokentype should be number
		assertEquals(Token.TokenType.NUMBER, myTokens.get(2).type);
	
		
		
		//<= should be in cell 3
		assertEquals(null, myTokens.get(3).tokenValue);
		//tokentype should be lessthanorequals
		assertEquals(Token.TokenType.LESSTHANOREQUALS, myTokens.get(3).type);
	
		
		//100 should be in cell 4
		assertEquals("100", myTokens.get(4).tokenValue);
		//tokentype should be number
		assertEquals(Token.TokenType.NUMBER, myTokens.get(4).type);
		
		
		//null should be in cell 5
		assertEquals(null, myTokens.get(5).tokenValue);
		//tokentype should be seperator
		assertEquals(Token.TokenType.SEPERATOR, myTokens.get(5).type);
		
		
		
		//78 should be in cell 6
		assertEquals("78", myTokens.get(6).tokenValue);
		//tokentype should be number
		assertEquals(Token.TokenType.NUMBER, myTokens.get(6).type);
		
		
		//null should be in cell 7
		assertEquals(null, myTokens.get(7).tokenValue);
		//tokentype should be seperator
		assertEquals(Token.TokenType.SEPERATOR, myTokens.get(7).type);
		
		
		
	
		
		
		TokenManager manageMyTokens = new TokenManager(obj.Lex());
		
		
		
		
		Token spot0 = new Token(Token.TokenType.WORD, 1, 8, "hello_34");
		Token spot1 = new Token(Token.TokenType.SEPERATOR, 1, 8);
		Token spot2 = new Token(Token.TokenType.NUMBER, 2, 3, "53");
		Token spot3 = new Token(Token.TokenType.LESSTHANOREQUALS, 2, 5);
		Token spot4 = new Token(Token.TokenType.NUMBER, 2, 8, "100");
		Token spot5 = new Token(Token.TokenType.SEPERATOR, 2, 9);
		Token spot6 = new Token(Token.TokenType.NUMBER, 2, 12, "78");
		Token spot7 = new Token(Token.TokenType.SEPERATOR, 2, 12);
		
		
		// tests peek and lineNum
		assertEquals(spot0.lineNum, manageMyTokens.Peek(0).orElseThrow().lineNum);
		assertEquals(spot1.lineNum, manageMyTokens.Peek(1).orElseThrow().lineNum);
		assertEquals(spot2.lineNum, manageMyTokens.Peek(2).orElseThrow().lineNum);
		assertEquals(spot3.lineNum, manageMyTokens.Peek(3).orElseThrow().lineNum);
		assertEquals(spot4.lineNum, manageMyTokens.Peek(4).orElseThrow().lineNum);
		assertEquals(spot5.lineNum, manageMyTokens.Peek(5).orElseThrow().lineNum);
		assertEquals(spot6.lineNum, manageMyTokens.Peek(6).orElseThrow().lineNum);
		assertEquals(spot7.lineNum, manageMyTokens.Peek(7).orElseThrow().lineNum);
		
		
		
		// tests MatchAndRemove
		assertEquals(spot0.type,manageMyTokens.MatchAndRemove(Token.TokenType.WORD).orElseThrow().type);
		assertEquals(spot1.type,manageMyTokens.MatchAndRemove(Token.TokenType.SEPERATOR).orElseThrow().type);
		assertEquals(spot2.type,manageMyTokens.MatchAndRemove(Token.TokenType.NUMBER).orElseThrow().type);
		assertEquals(spot3.type,manageMyTokens.MatchAndRemove(Token.TokenType.LESSTHANOREQUALS).orElseThrow().type);
		assertEquals(spot4.type,manageMyTokens.MatchAndRemove(Token.TokenType.NUMBER).orElseThrow().type);
		assertEquals(spot5.type,manageMyTokens.MatchAndRemove(Token.TokenType.SEPERATOR).orElseThrow().type);
		assertEquals(spot6.type,manageMyTokens.MatchAndRemove(Token.TokenType.NUMBER).orElseThrow().type);
		assertEquals(spot7.type,manageMyTokens.MatchAndRemove(Token.TokenType.SEPERATOR).orElseThrow().type);
	
		
		
		// tests MoreTokens, should return false if LinkedList is empty
		assertEquals(0, manageMyTokens.HowManyTokens());
		
	
		Lexer objTwo = new Lexer("function print_sumsq(x, y) {++x}");
		LinkedList<Token> myTokensTwo = objTwo.Lex();
		
		Parser parsedObj = new Parser(objTwo.Lex());
		ProgramNode program = parsedObj.parse();
		
		BlockNode statements = program.FUNCTION.getFirst().block;
		
		
		// tests ParseBottomLevel() and ParseLValue()
		assertEquals("PREINCx", statements.toString());
		
		
		
		
		
		
		Lexer objThree = new Lexer("function print_sumsq(x, y) {`[abc]`}");
		LinkedList<Token> myTokensThree = objThree.Lex();
		
		Parser parsedObjThree = new Parser(objThree.Lex());
		ProgramNode programThree = parsedObjThree.parse();
		
		
		BlockNode statementsThree = programThree.FUNCTION.getFirst().block;
		
		
		
		// tests ParseBottomLevel() and ParseLValue()
		assertEquals("REGULAREXPRESSION[abc]", statementsThree.toString());
		
		
		
		
		
		
		
		Lexer objFour = new Lexer("function print_sumsq(x, y) {e[abc]}");
		LinkedList<Token> myTokensFour = objFour.Lex();
		
		Parser parsedObjFour = new Parser(objFour.Lex());
		ProgramNode programFour = parsedObjFour.parse();
		
		
		BlockNode statementsFour = programFour.FUNCTION.getFirst().block;
		
		
		
		// tests ParseBottomLevel() and ParseLValue()
		assertEquals("e[Optional[abc]]", statementsFour.toString());
		
		
		
		
		
		
		
		
		Lexer postInc = new Lexer("x++");
		LinkedList<Token> tokenListPostInc = postInc.Lex();
		
		Parser parsedPostInc = new Parser(postInc.Lex());
		ProgramNode programPostInc = parsedPostInc.parse();
		
		
		BlockNode statementsPostInc = programPostInc.FUNCTION.getFirst().block;
		
		
		// tests ParseBottomLevel() and ParseLValue()
		assertEquals("e[Optional[abc]]", statementsPostInc.toString());
		
		
		
		
		
		
	}

}
