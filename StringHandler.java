
public class StringHandler {

	
	
	
	
	
	private String awkFile;       // string for the AWK file
	private int index = 0;        // holds the index of each individual token
	
	
	
	// class constructor, gets file from Lexer
	public StringHandler(String x) {
		
		awkFile = x;
		
	}
	
	
	
	
	
	// Peeks at the next character in the string
	public char Peek(int i) {
		
		// holds the char at index i
		char character;
		
		
		
		// only returns the character if the index is in bounds
		if (index < awkFile.length()) {
			character = awkFile.charAt(i + index);
			
			return character;
		
		}
		
		else {
			
			return '\0';
			
			
		}
	}
	
	
	// peeks at the next i characters in the string
	public String PeekString(int i) {
		
		// obtains next "i" characters
		String characters;
		
		// only returns the character if the index is in bounds
			if (index < awkFile.length()) {
				characters = awkFile.substring(index, i);
		
				return characters;
		
		
				}
		
		return "\0";
	}
	
	
	
	// gets the next character in the string
	public char GetChar() {
		
		// holds the next character
		char character;
		
		
		
		// only returns the character if the index is in bounds
		if (index < awkFile.length()) {
			character = awkFile.charAt(index);
		
		
			// increases index by 1
			index+=1;
		
			return character;
			
		}
		
		
		return '\0';
		
	}
	
	// moves the index i positions ahead
	public void Swallow(int i) {
		
		// moves index "i" positions forward
		index+=i;
		
	}
	
	
	// returns true is the index is at the end of file
	public boolean IsDone() {
		
		boolean atEnd;
		
		
		// if statement that detects if index is at end of the document
		if(index == awkFile.length()) {
			
			atEnd = true;
			
		}
		
		else {
			
			atEnd = false;
		}
		
		
		
		return atEnd;
	}
	
	
	// looks how much of the file is left after the index
	public String Remainder() {
		
		// holds rest of document
		String characters;
		characters = awkFile.substring(index);
		
		
		return characters;
		
	}
	
	
	
	
	
}
