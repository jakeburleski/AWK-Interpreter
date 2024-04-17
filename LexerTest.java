import static org.junit.jupiter.api.Assertions.*;



import java.util.LinkedList;

import org.junit.jupiter.api.Test;

class LexerTest {

	@Test
	//Tests awk code
	void awkTest() {
		//fail("Not yet implemented");
		
		
		var lexerTestOne = new Lexer("awk NF file\r\n"
				+ "CREDITS,EXPDATE,USER,GROUPS\r\n"
				+ "99,01 jun 2018,sylvain,team:::admin\r\n"
				+ "52,01    dec   2018,sonia,team\r\n"
				+ "52,01    dec   2018,sonia,team\r\n"
				+ "25,01    jan   2019,sonia,team\r\n"
				+ "10,01 jan 2019,sylvain,team:::admin\r\n"
				+ "8,12    jun   2018,öle,team:support\r\n"
				+ "17,05 apr 2019,abhishek,guest");
		
		
		System.out.println(lexerTestOne.Lex());
		
	}
		
		
		
	// tests underscore and numbers after word
	void lexerWordUnderScoreNumberTest() {
		var lexerWordUnderScoreNumberTest = new Lexer("hello_342");
			
		System.out.println(lexerWordUnderScoreNumberTest.Lex());
		}
		
		
		
		
		// tests a number after a word
	void lexerWordNumberTest() {
		var lexerWordNumberTest = new Lexer("goodmorning4542");
		
		System.out.println(lexerWordNumberTest.Lex());
	}
		
		
		
		// tests numbers than word
	void lexerNumberThenWordTest() {
		var lexerNumberThenWordTest = new Lexer("5674Goodbye");
		
		System.out.println(lexerNumberThenWordTest.Lex());
	}
		
		
		// tests symbols both one and 2 characters as well as unrecognized symbol
	void lexerSymbolsTest() {
		var lexerSymbolsTest = new Lexer("hello<<<=+-:$*===<\\");
				
		System.out.println(lexerSymbolsTest.Lex());
	}
		
		
		// tests string literals with quotation marks in string
	void lexerStringLiteralTest() {
		var lexerStringLiteralTest = new Lexer("The cat says \"meow\" then he ran \"away\" before saying \"merry \\\"christmas\\\" to all and to all a good night\"");
						
		System.out.println(lexerStringLiteralTest.Lex());
	}
		
		
		
		// tests string literals with quotation marks in string
	void lexerRegularExpressionsTest() {
		var lexerRegularExpressionsTest = new Lexer("The cat says `meow` then he ran `away` before saying `merry \\`christmas\\` to all and to all a good night`");
								
		System.out.println(lexerRegularExpressionsTest.Lex());
	}
		
		
		
		
		
		
		
		
	

}
