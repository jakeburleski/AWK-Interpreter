import java.util.LinkedList;
import java.util.Optional;

public class TokenManager {

	
	// LinkedList of tokens
	LinkedList<Token> tokenList = new LinkedList<>();
	
	
	// class constructor
	public TokenManager(LinkedList<Token> tokenList) {
		
		tokenList.removeLast();
		
		this.tokenList = tokenList;
	}
	
	// peek method to peek j tokens ahead
	public Optional<Token> Peek(int j){
		
		// returns token in spot j, as long as it doesn't go out of bounds
		if(!tokenList.get(j).equals(null)) {
			Token peekToken = tokenList.get(j);
		
			return Optional.of(peekToken);	
		}
		
		else {
			return Optional.empty();
		}
		
		
		
	}
	
	// returns true if tokenList is not empty
	public boolean MoreTokens() {
		
		if(tokenList.isEmpty()) {
			return false;
		}
		
		else {
			return true;
		}
		
	}
	
	// returns the number of tokens
	public int HowManyTokens() {
		
		return tokenList.size();
	}
	
	
	public Optional<Token> MatchAndRemove(Token.TokenType t){
		
		// compares head of tokenList's TokenType with that of TokenType argument
		if(tokenList.getFirst().type.equals(t)) {
			
			return Optional.of(tokenList.remove());
		}
		
		
		else {
			return Optional.empty();
		}
		
		
	}
	
	
	
	
}
