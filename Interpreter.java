import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {

	
	public HashMap<String, InterpreterDataType> globalVarMap = new HashMap<>();	// hashmap for global variables
	
	public HashMap<String, FunctionDefinitionNode> functionMap = new HashMap<>();	// hashmap for when someone calls a function
	
	
	
	ProgramNode program;
	LineManager manageMyLine;
	
	// for testing
	public Interpreter(){}

	
	public Interpreter(ProgramNode program, Optional<Path> opPath) {
		
		this.program = program;
		
		
		
		// try and catch statement to read file
		try {
			
			// if a path is provided call Files.readAllLines and create LineManager member,
			// sets FILENAME, and defaults for FS, OFMT, OFS, and ORS. Fills functionMap with program's FUNCTION linked list
			if(!opPath.equals(Optional.empty())) {
				Path myPath = opPath.get();
				
				List<String> fileLine = Files.readAllLines(myPath);
				manageMyLine = new LineManager(fileLine);
				
				String filename = myPath.getFileName().toString();
				globalVarMap.put("FILENAME", new InterpreterDataType(filename));
				
				// sets defaults
				globalVarMap.put("FS", new InterpreterDataType(" "));
				globalVarMap.put("OFMT", new InterpreterDataType("%.6g"));
				globalVarMap.put("OFS", new InterpreterDataType(" "));
				globalVarMap.put("ORS", new InterpreterDataType("\n"));
				
				// populates functionMap
				FunctionDefinitionNode holdFunction = program.getFUNCTION();
				while(!holdFunction.equals(null)) {
					functionMap.put(holdFunction.getFunctionName(),holdFunction);
					holdFunction = program.getFUNCTION();
				}
				
				
				
				functionMap.put("print", new BuiltInFunctionDefinitionNode("print", (parameters) -> {
					String holdParams = "";	// will hold all the parameters
					int index = 0;			// holds keys for the hashmap
					
					// while the keys in the IADT exist (while there are still more parameters)
					while (parameters.containsKey(Integer.toString(index))){
						holdParams += parameters.get(Integer.toString(index)).value + " ";
						index++;
					}
					System.out.println(holdParams);
					return "";
					}));
				
				functionMap.put("printf", new BuiltInFunctionDefinitionNode("printf", (parameters) -> {
					int index = parameters.size();					// holds size of hashmap
					String getFirst = parameters.get("0").value;	// holds the format section, ex: (“%d %d %d”
					Object[] holdParams = new Object[index];		// creates string array the size of the number of parameters
					
					
					// loops over hashmap getting all parameters
					for (int i=1; i<index; i++) {
						holdParams[i-1] = parameters.get(Integer.toString(i)).value;
					}
					System.out.printf(getFirst, holdParams);
					return "";
					}));
				
				
				
				functionMap.put("getline", new BuiltInFunctionDefinitionNode("getline", (parameters) -> {
					boolean getLine = manageMyLine.SplitAndAssign();
					return "";
				}));
				
				
				
				functionMap.put("next", new BuiltInFunctionDefinitionNode("next", (parameters) -> {
					boolean next = manageMyLine.SplitAndAssign();
					return"";
				}));
				
				
				functionMap.put("gsub", new BuiltInFunctionDefinitionNode("gsub", (parameters) -> {
					String whatToReplace = parameters.get("0").value;		// what to replace
					String ReplaceWithThis = parameters.get("1").value;		// what is replacing previous string
					String field;											// what field to do this on
					String result = "";
					
					// if there are 2 parameters then $0
					if(parameters.size()==2) {
						field = "$0";
						// regex operations
						Pattern pattern = Pattern.compile(whatToReplace);
						Matcher match = pattern.matcher(ReplaceWithThis);
						result = match.replaceAll(field);
					}
					// if field seperator is included
					else if(parameters.size()==3) {
						// regex operations
						field = parameters.get("2").value;
						Pattern pattern = Pattern.compile(whatToReplace);
						Matcher match = pattern.matcher(ReplaceWithThis);
						result = match.replaceAll(field);
					}
					
					return result;}));
				
				
				functionMap.put("index", new BuiltInFunctionDefinitionNode("index", (parameters) -> {
					String wholeText = parameters.get("0").value;	// entire string that is being searched in
					String searchFor = parameters.get("1").value;	// what is being searched for in wholeText
					
					int index = wholeText.indexOf(searchFor);		// gets index
					
					return Integer.toString(index);}));
				
				
				functionMap.put("length", new BuiltInFunctionDefinitionNode("length", (parameters) -> {
					String wholeText = parameters.get("0").value;	// entire string
					int length = wholeText.length();				// gets length
					
					return Integer.toString(length);}));
				
				
				functionMap.put("match", new BuiltInFunctionDefinitionNode("match", (parameters) -> {
					String whatToReplace = parameters.get("0").value;		// what to replace
					String replaceWithThis = parameters.get("1").value;		// what is replacing previous string
					
					
					// if there was a match found, return the first time it was seen
					if(replaceWithThis.indexOf(whatToReplace) != -1) {
						return Integer.toString(replaceWithThis.indexOf(whatToReplace));
					}
					
					// else return 0
					else {
						return "0";
					}}));
				
				
				functionMap.put("split", new BuiltInFunctionDefinitionNode("split", (parameters) -> {
					String whatToReplace = parameters.get("0").value;		// what to replace
					String ReplaceWithThis = parameters.get("1").value;		// what is replacing previous string
					String field;											// what field to do this on
					String result = "";
					
					// if there are 2 parameters then $0
					if(parameters.size()==2) {
						field = "$0";
						// regex operations
						Pattern pattern = Pattern.compile(whatToReplace);
						Matcher match = pattern.matcher(ReplaceWithThis);
						result = match.replaceFirst(field);
					}
					// if field seperator is included
					else if(parameters.size()==3) {
						// regex operations
						field = parameters.get("2").value;
						Pattern pattern = Pattern.compile(whatToReplace);
						Matcher match = pattern.matcher(ReplaceWithThis);
						result = match.replaceFirst(field);
					}
				
					return result;}));
				
				
				functionMap.put("sprintf", new BuiltInFunctionDefinitionNode("sprintf", (parameters) -> {
					int index = parameters.size();					// holds size of hashmap
					String getFirst = parameters.get("0").value;	// holds the format section, ex: (“%d %d %d”
					Object[] holdParams = new String[index];		// creates string array the size of the number of parameters
					
					// loops over hashmap getting all parameters
					for (int i=1; i<index; i++) {
						holdParams[i-1] = parameters.get(Integer.toString(i)).value;
					}
					String sprintf = String.format(getFirst, holdParams);
					return sprintf;
				}));
				
				
				functionMap.put("sub", new BuiltInFunctionDefinitionNode("sub", (parameters) -> {
					String whatToReplace = parameters.get("0").value;		// what to replace
					String ReplaceWithThis = parameters.get("1").value;		// what is replacing previous string
					String field;											// what field to do this on
					String result = "";
					
					// if there are 2 parameters then $0
					if(parameters.size()==2) {
						field = "$0";
						// regex operations
						Pattern pattern = Pattern.compile(whatToReplace);
						Matcher match = pattern.matcher(ReplaceWithThis);
						result = match.replaceFirst(field);
					}
					// if field seperator is included
					else if(parameters.size()==3) {
						// regex operations
						field = parameters.get("2").value;
						Pattern pattern = Pattern.compile(whatToReplace);
						Matcher match = pattern.matcher(ReplaceWithThis);
						result = match.replaceFirst(field);
					}
				
					return result;}));
				
				
				functionMap.put("substr", new BuiltInFunctionDefinitionNode("substr", (parameters) -> {
					String mainStr = parameters.get("0").value;		// gets string to be worked on
					String start = parameters.get("1").value;		// tells substr where to start at
					String size;									// tells how many characters long the substr should be
					String result = "";									// return string value
					
					// if there is no specification for size
					if(parameters.size()==2) {
						result = mainStr.substring(Integer.parseInt(start));
					}
					
					// if there is a specification for size, get substring, then cut again by size, then return
					else if(parameters.size()==3) {
						size = parameters.get("2").value;
						result = mainStr.substring(Integer.parseInt(start));
						result = result.substring(0, Integer.parseInt(size));
					}
					return result;
				}));
				
				
				functionMap.put("tolower", new BuiltInFunctionDefinitionNode("tolower", (parameters) -> {
					// transforms string to lowercase
					String holdStrToLower = parameters.get("0").value;
					holdStrToLower = holdStrToLower.toLowerCase();
					return holdStrToLower;
				}));
				
				
				functionMap.put("toupper", new BuiltInFunctionDefinitionNode("toupper", (parameters) -> {
					// transforms string to uppercase
					String holdStrToUpper = parameters.get("0").value;
					holdStrToUpper = holdStrToUpper.toUpperCase();
					return holdStrToUpper;
				}));
				
				
				
				
			}
			
			// if path is empty, create empty list of String
			else {	
				List<String> emptyFile = new ArrayList<>();
				LineManager manageMyLine = new LineManager(emptyFile);
			}
			
			
		} catch (IOException e) {	
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	// FOR TESTING: allows user to set the field seperator for InterpreterDataType with value
	public void setglobalVar(String set) {	
		globalVarMap.put("FS", new InterpreterDataType(set));	
	}
	

	// FOR TESTING: allows user to set the field seperator for InterpreterDataType without value
	public void setglobalVar() {	
		globalVarMap.put("FS", new InterpreterDataType());	
	}
	
	
	

	
	
	public class LineManager{
		
		List<String> stringList;		// line of every file stored in a list
		int FNR; 						// file number of records
		static int NR = 0;				// keeps track of NR, static because NR doesn't reset when reading multiple files
		
		
		// class constructor
		public LineManager(List<String> stringList) {
			this.stringList = stringList;
			FNR = 0;
		}
		
		
		// get the next line and split it by looking at global variable
		public boolean SplitAndAssign() {
			
			// while the string list is not empty, remove current line
			while(!stringList.isEmpty()) {
				
				String nextLine = stringList.remove(0);		// removes current heads line
				
				FNR++;		// increments FNR
				NR++;		// increments NR
				
				globalVarMap.put("FNR", new InterpreterDataType(Integer.toString(FNR)));	// updates FNR hashmap value
				globalVarMap.put("NR", new InterpreterDataType(Integer.toString(NR)));		// updates NR hashmap value
				
				
				// creates global variable for $0
				globalVarMap.put("$0", new InterpreterDataType(nextLine));
				
				// splits the line according to whatever value of FS is set to
				String[] currentLineList = nextLine.split(globalVarMap.get("FS").getValue());
				
				// counter for NF
				int counter = 0;
				
				// loops over the current line and assigns all $ global variables, as well as updating counter, NR, and FNR
				for(int i=0; i<currentLineList.length; i++) {
					String $StringValue = "$" + (i+1);
					globalVarMap.put($StringValue, new InterpreterDataType(currentLineList[i]));
					
					counter++;
					// assigns NF 
					globalVarMap.put("NF", new InterpreterDataType(Integer.toString(counter)));
				}
			}	
			return false;	
		}
	}
	
	public InterpreterDataType GetIDT(Node inputNode, Optional<HashMap<String, InterpreterDataType>> localVariables) throws Exception {
		
		// for assignment nodes
		if (inputNode instanceof AssignmentNode) {
			AssignmentNode assignment = (AssignmentNode)inputNode;						// type casts
			
			// checks is node is a variable reference node
			if(assignment.target instanceof VariableReferenceNode) {
				
				// gets the string value for the target value
				VariableReferenceNode targetVar = (VariableReferenceNode)assignment.target;
				String targetName = targetVar.getName();
				// calls getIDT recursively for right side of assignment
				InterpreterDataType rightSideAssignment = GetIDT(assignment.expression, localVariables);
				
				// if localVariables is present, add targetname to local variables, otherwise add to global variables
				if(!localVariables.equals(Optional.empty())) {
					localVariables.get().put(targetName, rightSideAssignment);
				}
				else {
					globalVarMap.put(targetName, rightSideAssignment);
				}
				return rightSideAssignment;
			}
			
			// if not varaible reference, check if its an operation node
			else if (assignment.target instanceof OperationNode) {
				OperationNode checkForSigil = (OperationNode)assignment.target;						// type casts
				
				// checks for the sigil
				if(checkForSigil.operation.equals(OperationNode.operations.DOLLAR)) {
					// calls GetIDT() on right side
					InterpreterDataType rightSideAssignment = GetIDT(assignment.expression, localVariables);
					// gets target
					InterpreterDataType operationTarget = GetIDT(checkForSigil, localVariables);
					
					// if localVariables is present, add targetname to local variables, otherwise add to global variables
					if(!localVariables.equals(Optional.empty())) {
						localVariables.get().put(operationTarget.getValue(), rightSideAssignment);
					}
					else {
						globalVarMap.put(operationTarget.getValue(), rightSideAssignment);
					}
					return rightSideAssignment;
				}
			}
			
			
			
		}
		// check is node is constant node
		else if (inputNode instanceof ConstantNode) {
			ConstantNode constant = (ConstantNode)inputNode;						// type casts
			InterpreterDataType constantIDT = GetIDT(constant, localVariables);
			return constantIDT;
		}
		
		// check is node is function call node
		else if (inputNode instanceof FunctionCallNode) {
			FunctionCallNode functionCall = (FunctionCallNode)inputNode;						// type casts
			String runFunctionCallVar = RunFunctionCall(functionCall, localVariables);			// calls RunFunctionCall
			InterpreterDataType functionCallIDT = new InterpreterDataType(runFunctionCallVar);	// Creates new IDT using return from line above
			return functionCallIDT;
		}
		
		// check is node is pattern node
		else if (inputNode instanceof PatternNode) {
			throw new Exception("Error: Cannot pass pattern to function or assignment");
		}
		
		// check is node is ternary node
		else if (inputNode instanceof TernaryNode) {
			TernaryNode ternary = (TernaryNode)inputNode;			// type casts
			InterpreterDataType conditionIDT = GetIDT(ternary.getCondition(), localVariables); 
				
			// if condition isn't 0 meaning condition is true, return true case, otherwise return false case
			if(Double.parseDouble(conditionIDT.getValue())!=0) {
				return GetIDT(ternary.trueCase, localVariables);
			}
			else{
				return GetIDT(ternary.falseCase, localVariables);
			}
		}
		
		
		// check is node is variable reference node
		else if (inputNode instanceof VariableReferenceNode) {
			VariableReferenceNode variableReference = (VariableReferenceNode)inputNode;						// type casts
			String varName = variableReference.getName();
			
			// if the variable is an array reference
			if(!variableReference.getIndex().equals(Optional.empty())) {
				// gets the index of array
				InterpreterDataType index = GetIDT(variableReference.getIndex().orElseThrow(), localVariables);
				
				// if the array is in the local variables, check if its an instance of IADT, then find index from array and return it
				if(!localVariables.equals(Optional.empty()) && localVariables.get().containsKey(varName)) {
					InterpreterDataType checkIfIADT = localVariables.get().get(varName);
					
					if(checkIfIADT instanceof InterpreterArrayDataType) {
						InterpreterArrayDataType array = (InterpreterArrayDataType) checkIfIADT;
						InterpreterDataType result = array.IADTmap.get(index.value);
						return result;
					}
					
					else {
						throw new Exception("Error: Array is not of type InterpreterArrayDataType");
					}
				}
				
			}
			// if variable is normal variable and not array reference
			else {
				// return value
				InterpreterDataType searchForVar;
				
				
				// looks up variable in global variable hashmap
				if(globalVarMap.containsKey(varName)) {
					searchForVar = globalVarMap.get(varName);
					return searchForVar;
				}
				else if(localVariables.get().containsKey(varName)) {
					searchForVar = localVariables.get().get(varName);
					return searchForVar;
				}
			}
			throw new Exception("Error: variable is not valid");
			
		}
		
		
		// check is node is operation node
		else if (inputNode instanceof OperationNode) {
			OperationNode operation = (OperationNode)inputNode;		// type casts
			Node leftOp = operation.left;							// left node
			Node rightOp;											// right node
			
			// evaluates left node
			InterpreterDataType holdLeftIDT = GetIDT(leftOp, localVariables);
			
			
			
			
			// if there is a right node evaluate right node
			if(!operation.right.equals(Optional.empty())) {
				rightOp = operation.right.get();
				
				// checks if operation is match or notmatch
				if(operation.operation.equals(OperationNode.operations.MATCH) || operation.operation.equals(OperationNode.operations.NOTMATCH)) {
					// if right node is not pattern, throw new exception
					if(!(rightOp instanceof PatternNode)) {
						throw new Exception("Error: right node is not an instance of Pattern Node");
					}
					
					PatternNode right = (PatternNode)rightOp;	// right pattern node
					String regex = right.getPattern();			// regular expression
					String left = holdLeftIDT.getValue();		// left node
					
					Pattern pattern = Pattern.compile(regex);	// takes the pattern from regex
					Matcher matcher = pattern.matcher(left);	// matches pattern with the left node
					
					// for match
					if(operation.operation.equals(OperationNode.operations.MATCH)) {
						// if a match is found, return where it was found, otherwise return 0(false)
						if(matcher.find()) {
							return new InterpreterDataType(Integer.toString(matcher.start()));
						}
						else {return new InterpreterDataType("0");}
					}
					
					// for notmatch
					else if(operation.operation.equals(OperationNode.operations.NOTMATCH)) {
						// if a match is found, return 0(false), otherwise return 1(true)
						if(matcher.find()) {
							return new InterpreterDataType("0");
						}
						else {return new InterpreterDataType("1");}
					}
					
				}
				
				// for in
				if(operation.operation.equals(OperationNode.operations.IN)) {
					
					
					if(!(rightOp instanceof VariableReferenceNode)) {
						throw new Exception("Error: Right node is not a variable reference");
					}
					
					InterpreterDataType holdRightIDT = GetIDT(rightOp, localVariables);
					// if the array is in the local variables, check if its an instance of IADT, then find index from array and return it
					if(!localVariables.equals(Optional.empty()) && localVariables.get().containsKey(holdRightIDT.value)) {
						InterpreterDataType checkIfIADT = localVariables.get().get(holdRightIDT.value);
						
						if(checkIfIADT instanceof InterpreterArrayDataType) {
							InterpreterArrayDataType array = (InterpreterArrayDataType) checkIfIADT;
							
							
							InterpreterDataType result = array.IADTmap.get(holdLeftIDT.value);
							return result;
						}
						
						else {
							throw new Exception("Error: Array is not of type InterpreterArrayDataType");
						}
					
					}
				}
				
				
				
				
				
				
				
				
				
				
				InterpreterDataType holdRightIDT = GetIDT(rightOp, localVariables);
				
				Optional<Float> leftFloat = convertStrToFloat(holdLeftIDT.getValue());
				Optional<Float> rightFloat = convertStrToFloat(holdRightIDT.getValue());
				
				
				
				// if values are a string
				if(leftFloat.equals(Optional.empty()) && rightFloat.equals(Optional.empty())) {
					
					// turn left and right nodes into strings
					String first = holdLeftIDT.getValue();
					String second = holdRightIDT.getValue();
					
					
					
					// for concatenation
					if(operation.operation.equals(OperationNode.operations.CONCATENATION)) {
						String strConcat = first+second;
						return new InterpreterDataType(strConcat);
					}
					
					// for all compare operations
					if(operation.operation.equals(OperationNode.operations.EQ)) {
						if(first.equals(second)) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.NE)) {
						if(!first.equals(second)) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.LT)) {
						if(first.length() < second.length()) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.LE)) {
						if(first.length() <= second.length()) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.GT)) {
						if(first.length() > second.length()) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.GE)) {
						if(first.length() >= second.length()) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					
					
					// for boolean operations (will always be false if they cant be converted to a float
					if(operation.operation.equals(OperationNode.operations.AND)) {
						return new InterpreterDataType("0");
					}
					if(operation.operation.equals(OperationNode.operations.OR)) {
						return new InterpreterDataType("0");
					}
					if(operation.operation.equals(OperationNode.operations.NOT)) {
						return new InterpreterDataType("0");
					}
					
					
					
				}
				
				// if values are a float
				else if(!leftFloat.equals(Optional.empty()) && !rightFloat.equals(Optional.empty())) {
					
					Float first = leftFloat.get();
					Float second = rightFloat.get();

					
					
					//EXPONENT,ADD,SUBTRACT,MULTIPLY,DIVIDE,MODULO
					
					// for exponent
					if(operation.operation.equals(OperationNode.operations.EXPONENT)) {
						double result = Math.pow(first, second);
						return new InterpreterDataType(String.valueOf(result));
					}

					// for add
					if(operation.operation.equals(OperationNode.operations.ADD)) {
						Float result = first + second;
						return new InterpreterDataType(String.valueOf(result));
					}

					// for subtract
					if(operation.operation.equals(OperationNode.operations.SUBTRACT)) {
						Float result = first - second;
						return new InterpreterDataType(String.valueOf(result));
					}

					// for mulitply
					if(operation.operation.equals(OperationNode.operations.MULTIPLY)) {
						Float result = first * second;
						return new InterpreterDataType(String.valueOf(result));
					}

					// for modulo
					if(operation.operation.equals(OperationNode.operations.MODULO)) {
						Float result = first % second;
						return new InterpreterDataType(String.valueOf(result));
					}

					// for subtract
					if(operation.operation.equals(OperationNode.operations.SUBTRACT)) {
						Float result = first - second;
						return new InterpreterDataType(String.valueOf(result));
					}
					
					
					
					// for all compare operations
					if(operation.operation.equals(OperationNode.operations.EQ)) {
						if(first == second) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.NE)) {
						if(first != second) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.LT)) {
						if(first < second) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.LE)) {
						if(first <= second) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.GT)) {
						if(first > second) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.GE)) {
						if(first >= second) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					
					
					
					// for boolean operations
					if(operation.operation.equals(OperationNode.operations.AND)) {
						if(first!=0 && second!=0) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.OR)) {
						if(first!=0 || second!=0) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
					if(operation.operation.equals(OperationNode.operations.NOT)) {
						if(first==0) {return new InterpreterDataType("1");}
						else {return new InterpreterDataType("0");}
					}
				}	
			}
			
			
			
			// if there is no right node
			else if(operation.right.equals(Optional.empty())){
				
				
				// handles field references $
				if(operation.operation.equals(OperationNode.operations.DOLLAR)) {
					holdLeftIDT.value = "$" + holdLeftIDT.value;
					InterpreterDataType variable = globalVarMap.get(holdLeftIDT.value);
					return variable;
				}
				
				
				
				// for preinc
				if(operation.operation.equals(OperationNode.operations.PREINC)){
					// try catch in case number is not parseable
					try {
						int num = Integer.parseInt(holdLeftIDT.value);
						++num;
						return new InterpreterDataType(String.valueOf(num));
					} catch(NumberFormatException e) {
						throw new Exception("Error: not a valid int");
					}
				}
				// for postinc
				if(operation.operation.equals(OperationNode.operations.POSTINC)){
					// try catch in case number is not parseable
					try {
						int num = Integer.parseInt(holdLeftIDT.value);
						num++;
						return new InterpreterDataType(String.valueOf(num));
					} catch(NumberFormatException e) {
						throw new Exception("Error: not a valid int");
					}
				}
				// for predec
				if(operation.operation.equals(OperationNode.operations.PREDEC)){
					// try catch in case number is not parseable
					try {
						int num = Integer.parseInt(holdLeftIDT.value);
						--num;
						return new InterpreterDataType(String.valueOf(num));
					} catch(NumberFormatException e) {
						throw new Exception("Error: not a valid int");
					}
				}
				// for postdec
				if(operation.operation.equals(OperationNode.operations.POSTDEC)){
					// try catch in case number is not parseable
					try {
						int num = Integer.parseInt(holdLeftIDT.value);
						num--;
						return new InterpreterDataType(String.valueOf(num));
					} catch(NumberFormatException e) {
						throw new Exception("Error: not a valid int");
					}
				}
				// for unarypos
				if(operation.operation.equals(OperationNode.operations.UNARYPOS)){
					// try catch in case number is not parseable
					try {
						int num = Integer.parseInt(holdLeftIDT.value);
						// if the number is negative, make it positive
						if(num<0) {num = -num;}
						return new InterpreterDataType(String.valueOf(num));
					} catch(NumberFormatException e) {
						throw new Exception("Error: not a valid int");
					}
				}
				// for unaryneg
				if(operation.operation.equals(OperationNode.operations.UNARYNEG)){
					// try catch in case number is not parseable
					try {
						int num = Integer.parseInt(holdLeftIDT.value);
						// if the number is positive, make it negative
						if(num>0) {num = -num;}
						return new InterpreterDataType(String.valueOf(num));
					} catch(NumberFormatException e) {
						throw new Exception("Error: not a valid int");
					}
				}
			}	
		}
		
		throw new Exception("Error: Went through all of GetIDT");
		
	}
	
	// run Function call
	public String RunFunctionCall(FunctionCallNode functionCallNode, Optional<HashMap<String, InterpreterDataType>> localVariables) throws Exception {
		
		// finds the function defintion
		FunctionDefinitionNode functionDefNode = program.getFUNCTION();
		
		// if functiondef node and functioncall node don't have same number of parameters, throw and error
		if(functionDefNode.parameterNames.size() != functionCallNode.parameters.size()) {throw new Exception("Error: Unequal size of parameters");}
		
		// create a new HashMap to populate
		HashMap<String, InterpreterDataType> myMap = new HashMap<>();
		
		// for all parameters in functionDefNode fill HashMap with parameters, mapped to functionCallNodes IDT
		int i=0;
		for(Optional<Token> parameter : functionDefNode.parameterNames) {
			// makes sure we aren't on the last variable
			if(i < functionCallNode.parameters.size()) {
				myMap.put(parameter.get().tokenValue, GetIDT(functionCallNode.parameters.get(i), localVariables));
			}
			// for last variable of variadic
			else {
				// create arraylist for remaining values
				ArrayList<InterpreterDataType> variadic = new ArrayList<>();
			
				// iterates through remainging values and adds values to variadic arraylist
				for(int j=i; j<functionCallNode.parameters.size(); j++) {
					variadic.add(GetIDT(functionCallNode.parameters.get(j), localVariables));
				}
				// creates IADT for remainging value to be able to be added to myMap
				InterpreterArrayDataType lastValue = new InterpreterArrayDataType();
				for(int k=0; k<variadic.size(); k++) {
					lastValue.IADTmap.put(parameter.get().tokenValue, GetIDT(functionCallNode.parameters.get(k), localVariables));
				}
				// puts last value into map
				myMap.put(parameter.get().tokenValue, lastValue);
			}
			
			
			
			i++;
		}
		
		// if functionDefNode is a BIFDN execute the lambda on myMap and return value
		if(functionDefNode instanceof BuiltInFunctionDefinitionNode) {
			BuiltInFunctionDefinitionNode builtIn = (BuiltInFunctionDefinitionNode)functionDefNode;
			String holdMyString = builtIn.execute.apply(myMap);
			return holdMyString;
		}
		
		// else functionDefNode is not BIFDN, call InterpretListOfStatements on functionDefNodes statements with myMap and return ReturnTypes string
		else {
			ReturnType checkReturn = InterpretListOfStatements(functionDefNode.block.statements, myMap);
			return checkReturn.returnValue.get();
			
		}
		
		
	}
	
	// helper method to convert string to float for operation nodes
	public Optional<Float> convertStrToFloat(String value) { 
		try {
			return Optional.of(Float.parseFloat(value));
		} catch(NumberFormatException e) {
			return Optional.empty();
		}
		
	}
	
	
	
			
	// processStatement method
	public ReturnType ProcessStatement(HashMap<String, InterpreterDataType> locals, StatementNode stmt) throws Exception {
			
		// if break node, return returntype with event break
		if(stmt instanceof BreakNode) {
			return new ReturnType(ReturnType.event.BREAK);
		}
			
		// if continue node, return returntype with event continue
		else if(stmt instanceof ContinueNode) {
			return new ReturnType(ReturnType.event.CONTINUE);
		}
			
		// if delete node
		else if(stmt instanceof DeleteNode) {
				
			DeleteNode whatToDelete = (DeleteNode)stmt;						//changes node to delete node
			String deleteThis = whatToDelete.whatToDelete.getName();		// gets string name
			Optional<Node> index = whatToDelete.whatToDelete.getIndex();	// gets optional index of array
				
				
			// checks if the array is in local variables
			if(locals.containsKey(deleteThis)) {
					
				if(!index.equals(Optional.empty())) {
					locals.remove(index.get().toString());
				}
				else { locals.remove(deleteThis);}
			}

			// checks if the array is in global variables
			else if(globalVarMap.containsKey(deleteThis)) {
					
				if(!index.equals(Optional.empty())) {
					globalVarMap.remove(index.get().toString());
				}
				else { globalVarMap.remove(deleteThis);}
			}
		}
			
		// evaluates DoWhile nodes
		else if(stmt instanceof DoWhileNode) {
				
				
			DoWhileNode doWhile = (DoWhileNode)stmt;
			InterpreterDataType condition = GetIDT(doWhile.condition, Optional.of(locals));
			// do while loop
			do {
					
				// calls InterpretListOfStatements
				ReturnType result = InterpretListOfStatements(doWhile.statement.statements, locals);
					
				// looks for break
				if(result.whatHappened.equals(ReturnType.event.BREAK)) {
					break;
				}
					
					
					
				// get condition
				condition = GetIDT(doWhile.condition, Optional.of(locals));
					
			}while(!condition.getValue().equals("0"));
				
			return new ReturnType(ReturnType.event.NORMAL);	
		}
			
			
		// if node is a for node
		else if(stmt instanceof ForNode) {
				
			ForNode stmtForNode = (ForNode)stmt;
			
			// checks if there is an initial, and calls ProcessStatement
			if(stmtForNode.startVar instanceof StatementNode) {
				ProcessStatement(locals, (StatementNode)stmtForNode.iteration);
			}
				
			// by AWK rules, while condition does not equal 0
			while(!GetIDT(stmtForNode.condition, Optional.of(locals)).getValue().equals("0")) {
					

				// calls InterpretListOfStatements
				ReturnType result = InterpretListOfStatements(stmtForNode.body.statements, locals);

				// looks for break
				if(result.whatHappened.equals(ReturnType.event.BREAK)) {
					break;
				}
					
					
				if(stmtForNode.iteration instanceof StatementNode) {
					ReturnType iteration = ProcessStatement(locals, (StatementNode)stmtForNode.iteration);
				}
			}
				
			return new ReturnType(ReturnType.event.NORMAL);
				
		}
			
			
		else if (stmt instanceof ForEachNode) {
				
			ForEachNode forEach = (ForEachNode)stmt;
				
			// gets array of forEach loop
			InterpreterDataType arrayIDT = GetIDT(forEach.arrayVar, Optional.of(locals));
				
			// makes sure array is of type IADT 
			if(arrayIDT instanceof InterpreterArrayDataType) {
				InterpreterArrayDataType arrayIADT = (InterpreterArrayDataType)arrayIDT;
					
				// loops through arrays hashmap
				arrayIADT.IADTmap.forEach((key, value)->{
					// sets variable to key
					String myKey = key;
						
					// goes through statements of foreach loop using try catch
					Optional<ReturnType> statement = Optional.empty();
					try {
						statement = Optional.of(InterpretListOfStatements(forEach.body.statements, locals));
					} catch (Exception e) {
						e.printStackTrace();
					}

					// looks for break
					if(statement.get().whatHappened.equals(ReturnType.event.BREAK)) {
						return; 
					}	
				});
			}
			else {throw new Exception("Error: ForEach array variable was not of type IADT");}
		}
			
			
		else if(stmt instanceof IfNode) {
				
			IfNode stmtIfNode = (IfNode)stmt;
			// gets condition of if node
			InterpreterDataType condition = GetIDT(stmtIfNode.condition, Optional.of(locals));
				
			// if the condition is not false call InterpretListOfStatements and return it if type is not normal
			if(!condition.getValue().equals("0") || condition.getValue().equals(null)) {
				ReturnType myReturn = InterpretListOfStatements(stmtIfNode.statement.statements, locals);
				if(!myReturn.whatHappened.equals(ReturnType.event.NORMAL)) {
					return myReturn;
				}
			}
				
			// else if there is an else if statement
			else if(!stmtIfNode.elseIf.equals(Optional.empty())) {
					
				// makes new IfNode for elseIf
				if(stmtIfNode.elseIf.get() instanceof IfNode) {
					IfNode elseIf = (IfNode) stmtIfNode.elseIf.get();
					// calls ProcessStatement on elseIf to recursively check for more elseIf statements
					ProcessStatement(locals, elseIf);
				}
					
					
					
					
			}
				
				
				
		}
			
			
			
		else if(stmt instanceof ReturnNode) {
			ReturnNode stmtReturnNode = (ReturnNode)stmt;
				
			// if there is a return value
			if(!stmtReturnNode.parseOp.equals(Optional.empty())) {
				// makes a new ReturnType with event RETURN and what to return with
				ReturnType holdReturn = new ReturnType(ReturnType.event.RETURN, Optional.of(GetIDT(stmtReturnNode.parseOp.get(), Optional.of(locals)).getValue()));	
			}
				
			// if there is now return value
			else {return new ReturnType(ReturnType.event.RETURN);}
		}
			
			
		else if(stmt instanceof WhileNode) {

			WhileNode While = (WhileNode)stmt;
			InterpreterDataType condition = GetIDT(While.condition, Optional.of(locals));
			// while loop
			while(!condition.getValue().equals("0")) {
					
				// calls InterpretListOfStatements
				ReturnType result = InterpretListOfStatements(While.statement.statements, locals);
					
				// looks for break
				if(result.whatHappened.equals(ReturnType.event.BREAK)) {
					break;
				}
					
					
					
				// get condition
				condition = GetIDT(While.condition, Optional.of(locals));
				
			}
				
			return new ReturnType(ReturnType.event.NORMAL);	
		}
			
		// for assignment and function call nodes, will throw an exception inside of GetIDt if not valid value is provided
		else {
			InterpreterDataType assignOrFunction = GetIDT(stmt, Optional.of(locals));
			return new ReturnType(ReturnType.event.NORMAL, Optional.of(assignOrFunction.getValue()));
		}
			
		throw new Exception("Error: ProccessStatement did not return a valid ReturnType");
			
			
	}
		
	
		
	public ReturnType InterpretListOfStatements(LinkedList<StatementNode> statements, HashMap<String, InterpreterDataType> locals) throws Exception {
			
		Optional<ReturnType> result = Optional.empty();
				
		for(StatementNode statement : statements) {
					
			result = Optional.of(ProcessStatement(locals, statement));
					
			if(!result.equals(Optional.empty())) {
				return result.get();
			}
		}
			
		throw new Exception("Error: Error in InterpretListOfStatements");
	}
			
	
	
	// interpret program
	public void InterpretProgram() throws Exception {
		
		// loops through programs begin blocks and calls InterpretBlock for each
		for(int i=0; i<program.BEGIN.size(); i++) {
			InterpretBlock(program.BEGIN.get(i));

			// SplitAndAssign
			manageMyLine.SplitAndAssign();
			
			// loops through programs other blocks for every record (FNR) and calls InterpretBlock for each
			for(int j=0; j<manageMyLine.FNR; j++) {
				InterpretBlock(program.OTHER.get(j));
			}
		}
		
		// loops through programs end blocks and calls InterpretBlock for each
		for(int i=0; i<program.END.size(); i++) {
			InterpretBlock(program.END.get(i));
		}
	}
	
	
	// interpret block
	public void InterpretBlock(BlockNode block) throws Exception {
		
		// if there is a condition, test if true, if true then process blocks statements
		if(!block.condition.equals(Optional.empty())) {
			
			if(GetIDT(block, Optional.empty()).getValue().equals("1")) {
				
				for(int i=0; i<block.statements.size(); i++) {
					ProcessStatement(globalVarMap, block.statements.get(i));
				}
			}
		}
		// if there is no condition, process statement
		else {
			for(int i=0; i<block.statements.size(); i++) {
				ProcessStatement(globalVarMap, block.statements.get(i));
			}
		}
		
		
	}
	
	
	
	
}
