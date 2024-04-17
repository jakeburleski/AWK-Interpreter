import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterpreterTest {

	
	public static void main(String[] args) throws Exception {
	
		/*Node operation = new OperationNode(new VariableReferenceNode("a"),Optional.of(new OperationNode(new ConstantNode("2"), Optional.of(new ConstantNode("2")) , OperationNode.operations.ADD)), OperationNode.operations.EQ);
		Interpreter test = new Interpreter();
		
		InterpreterDataType hold = test.GetIDT(operation, null);
		HashMap<String, InterpreterDataType> globalVarMap = test.globalVarMap;
		System.out.print(globalVarMap.toString());
		*/
		
		Interpreter testInter = new Interpreter(new ProgramNode(), Optional.empty());
		Interpreter.LineManager manageMyLine;
		
		
		
		// tests length lambda
		HashMap<String, InterpreterDataType> testLength = new HashMap<>();
		testLength.put("0", new InterpreterDataType("test"));
		// same lambda as one in interpreter constructor
		Function<HashMap<String, InterpreterDataType>, String> executeLength = (parameters) -> {
			String wholeText = parameters.get("0").value;	// entire string
			int length = wholeText.length();				// gets length
			
			return Integer.toString(length);};
		BuiltInFunctionDefinitionNode testInterpreter = new BuiltInFunctionDefinitionNode("length", executeLength );
		System.out.println("length: " + testInterpreter.execute.apply(testLength));
		
		
		

		// tests print lambda
		HashMap<String, InterpreterDataType> testPrint = new HashMap<>();
		testPrint.put("0", new InterpreterDataType("test"));
		// same lambda as one in interpreter constructor
		Function<HashMap<String, InterpreterDataType>, String> executePrint = (parameters) -> {
			String holdParams = "";	// will hold all the parameters
			int index = 0;			// holds keys for the hashmap
			
			// while the keys in the IADT exist (while there are still more parameters)
			while (parameters.containsKey(Integer.toString(index))){
				holdParams += parameters.get(Integer.toString(index)).value + " ";
				index++;
			}
			System.out.println(holdParams);
			return "";
			};
		System.out.print("print: ");
		testInterpreter = new BuiltInFunctionDefinitionNode("print", executePrint );
		System.out.print(testInterpreter.execute.apply(testPrint));
		
		
		
		
		
		
		

		// tests printf lambda
		HashMap<String, InterpreterDataType> testPrintf = new HashMap<>();
		testPrintf.put("0", new InterpreterDataType("%s is the number"));
		testPrintf.put("1", new InterpreterDataType("5"));
		
		// same lambda as one in interpreter constructor
		Function<HashMap<String, InterpreterDataType>, String> executePrintf = (parameters) -> {
			int index = parameters.size();					// holds size of hashmap
			String getFirst = parameters.get("0").value;	// holds the format section, ex: (“%d %d %d”
			Object[] holdParams = new Object[index];		// creates string array the size of the number of parameters
			
			// loops over hashmap getting all parameters
			for (int i=1; i<index; i++) {
				holdParams[i-1] = parameters.get(Integer.toString(i)).value;
			}
			System.out.printf(getFirst, holdParams);
			return "";
			};
		
		System.out.print("printf: ");
		testInterpreter = new BuiltInFunctionDefinitionNode("printf", executePrintf );
		testInterpreter.execute.apply(testPrintf);
		
		
		
		
		

		// tests gsub lambda
		HashMap<String, InterpreterDataType> testGsub = new HashMap<>();
		testGsub.put("0", new InterpreterDataType("test"));
		testGsub.put("1", new InterpreterDataType("replaced"));
		// same lambda as one in interpreter constructor
		Function<HashMap<String, InterpreterDataType>, String> executeGsub = (parameters) ->{
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
			
			return result;};

			
			testInterpreter = new BuiltInFunctionDefinitionNode("gsub", executeGsub );
			System.out.println("\ngsub: " + testInterpreter.execute.apply(testGsub));
			
		
		
			
			
			
			
			
			

			// tests index lambda
			HashMap<String, InterpreterDataType> testIndex = new HashMap<>();
			testIndex.put("0", new InterpreterDataType("test"));
			testIndex.put("1", new InterpreterDataType("s"));
			// same lambda as one in interpreter constructor
			Function<HashMap<String, InterpreterDataType>, String> executeIndex = (parameters) ->{
				String wholeText = parameters.get("0").value;	// entire string that is being searched in
				String searchFor = parameters.get("1").value;	// what is being searched for in wholeText
				
				int index = wholeText.indexOf(searchFor);		// gets index
				
				return Integer.toString(index);};
			

			testInterpreter = new BuiltInFunctionDefinitionNode("index", executeIndex );
			System.out.println("index: " + testInterpreter.execute.apply(testIndex));
			
			
			

			// tests match lambda
			HashMap<String, InterpreterDataType> testMatch = new HashMap<>();
			testMatch.put("0", new InterpreterDataType("123"));
			testMatch.put("1", new InterpreterDataType("test 123"));
			// same lambda as one in interpreter constructor
			Function<HashMap<String, InterpreterDataType>, String> executeMatch = (parameters) ->{
				String whatToReplace = parameters.get("0").value;		// what to replace
				String replaceWithThis = parameters.get("1").value;		// what is replacing previous string
				
				
				// if there was a match found, return the first time it was seen
				if(replaceWithThis.indexOf(whatToReplace) != -1) {
					return Integer.toString(replaceWithThis.indexOf(whatToReplace));
				}
				
				// else return 0
				else {
					return "0";
				}};

				testInterpreter = new BuiltInFunctionDefinitionNode("match", executeMatch );
				System.out.println("match: " + testInterpreter.execute.apply(testMatch));
				
		
		
		

				// tests split lambda
				HashMap<String, InterpreterDataType> testSplit = new HashMap<>();
				testSplit.put("0", new InterpreterDataType("123"));
				testSplit.put("1", new InterpreterDataType("test"));
				// same lambda as one in interpreter constructor
				Function<HashMap<String, InterpreterDataType>, String> executeSplit = (parameters) ->{
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
				
					return result;};

					testInterpreter = new BuiltInFunctionDefinitionNode("split", executeSplit );
					System.out.println("split: " + testInterpreter.execute.apply(testSplit));
					
					
					
					

				// tests sprintf lambda
				HashMap<String, InterpreterDataType> testSprintf = new HashMap<>();
				testSprintf.put("0", new InterpreterDataType("%s is the number"));
				testSprintf.put("1", new InterpreterDataType("5"));
				// same lambda as one in interpreter constructor
				Function<HashMap<String, InterpreterDataType>, String> executeSprintf = (parameters) ->{
					int index = parameters.size();					// holds size of hashmap
					String getFirst = parameters.get("0").value;	// holds the format section, ex: (“%d %d %d”
					Object[] holdParams = new String[index];		// creates string array the size of the number of parameters
					
					// loops over hashmap getting all parameters
					for (int i=1; i<index; i++) {
						holdParams[i-1] = parameters.get(Integer.toString(i)).value;
					}
					String sprintf = String.format(getFirst, holdParams);
					return sprintf;
				};
					
					

				testInterpreter = new BuiltInFunctionDefinitionNode("sprintf", executeSprintf );
				System.out.println("sprintf: " + testInterpreter.execute.apply(testSprintf));
				
				
				
				

				// tests sub lambda
				HashMap<String, InterpreterDataType> testSub = new HashMap<>();
				testSub.put("0", new InterpreterDataType("test 123"));
				testSub.put("1", new InterpreterDataType("123"));
				// same lambda as one in interpreter constructor
				Function<HashMap<String, InterpreterDataType>, String> executeSub = (parameters) ->{
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
				
					return result;};
				

				testInterpreter = new BuiltInFunctionDefinitionNode("sub", executeSub );
				System.out.println("sub: " + testInterpreter.execute.apply(testSub));
					
				
				
				
				
				
				
				
				

				// tests substr lambda
				HashMap<String, InterpreterDataType> testSubstr = new HashMap<>();
				testSubstr.put("0", new InterpreterDataType("test 123"));
				testSubstr.put("1", new InterpreterDataType("3"));
				// same lambda as one in interpreter constructor
				Function<HashMap<String, InterpreterDataType>, String> executeSubstr = (parameters) ->{
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
				};
				
				testInterpreter = new BuiltInFunctionDefinitionNode("substr", executeSubstr );
				System.out.println("substr: " + testInterpreter.execute.apply(testSubstr));
					
				
		
				

				// tests tolower lambda
				HashMap<String, InterpreterDataType> testTolower = new HashMap<>();
				testTolower.put("0", new InterpreterDataType("TEST 123"));
				// same lambda as one in interpreter constructor
				Function<HashMap<String, InterpreterDataType>, String> executeTolower = (parameters) ->{
					// transforms string to lowercase
					String holdStrToLower = parameters.get("0").value;
					holdStrToLower = holdStrToLower.toLowerCase();
					return holdStrToLower;
				};
				

				testInterpreter = new BuiltInFunctionDefinitionNode("tolower", executeTolower );
				System.out.println("tolower: " + testInterpreter.execute.apply(testTolower));
					
				
				

				// tests toupper lambda
				HashMap<String, InterpreterDataType> testToupper = new HashMap<>();
				testToupper.put("0", new InterpreterDataType("test 123"));
				// same lambda as one in interpreter constructor
				Function<HashMap<String, InterpreterDataType>, String> executeToupper = (parameters) ->{
					// transforms string to uppercase
					String holdStrToUpper = parameters.get("0").value;
					holdStrToUpper = holdStrToUpper.toUpperCase();
					return holdStrToUpper;
				};
				

				testInterpreter = new BuiltInFunctionDefinitionNode("toupper", executeToupper );
				System.out.println("toupper: " + testInterpreter.execute.apply(testToupper));
					
				
				
		
	}
	
	
	
	
	
	
}


/* for int 1, acreate a new hashmap of string to idt, load hsahmap with parameters you want to test.
 for ex testing length lambda, put in hashmap, at position put idt witha  string that you want to measure
 
 at key "0" put idt with string "test"
 param put ("0", idt("test")
 
 functionlength.get("test")
 will get lambda
 (hashmap)functionsdef.get("length").execute.apply(params);
 
 .apply is a lambda is a method that executes lambda
 
 */
