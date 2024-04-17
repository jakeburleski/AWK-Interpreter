import java.util.Optional;


public class ReturnType{
		
		
		enum event{NORMAL, BREAK, CONTINUE, RETURN}			// enum for event
		
		Optional<String> returnValue = Optional.empty();	// optional string for return type
		public event whatHappened;									// field to store enum
		
		
		
		// constructor for return type
		public ReturnType(event whatHappened, Optional<String> returnValue) {
			this.whatHappened = whatHappened;
			this.returnValue = returnValue;
		}
		

		// constructor for no return type
		public ReturnType(event whatHappened) {
			this.whatHappened = whatHappened;
		}
		
		// ToString
		public String toString() {
			if(returnValue.equals(Optional.empty())) {
				return whatHappened.name();
			}
			
			else {
				return whatHappened.name() + " " + returnValue.get();
			}
		}
}
	
	