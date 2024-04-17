import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class mainClass {
	
	public static void main(String[] args) {
		
		
		
		Path myPath = Paths.get("src/someFile.awk");    // creates path
		String content = "";                        // string that holds text from file
		
		
		// try and catch statement to read all bytes
		try {
			content = new String(Files.readAllBytes(myPath));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
		// creates lexer object and calls the lex method
		Lexer obj = new Lexer(content);
		obj.Lex();
		
		
		
		
		
		
		Parser parsedObj = new Parser(obj.Lex());
		ProgramNode program = parsedObj.parse();
		
		Interpreter myInterpret = new Interpreter(program, Optional.empty());
		
		
		
		
		
		
		LinkedList<StatementNode> statements = program.FUNCTION.getFirst().block.statements;
		
	
		System.out.println("Statements:\n");
		
		
		for(int i = 0; i < statements.size(); i ++) {
			
			System.out.println(statements.get(i).toString() + ", ");
		}
		
		
		
		System.out.print("\n\nFunctions:\n\n" + program.toString());
		
			
		
		
		// Copies the linked list from the lexer object
		LinkedList<Token> tokenList = obj.tokenValues;
		

		
			
			
		// prints out all tokens in the linked list
		for(int x = 0; x < tokenList.size(); x++)
		System.out.print(tokenList.get(x) + " ");
			
			
			
		
		
		
	}
	
	

}
