import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;

public abstract class Node {

	
	public abstract String toString();
	
}


// class for root node program
class ProgramNode extends Node{

	// ProgramNode class constructor
	public ProgramNode(LinkedList<Token> tokenList) {
		
	}
	
	// for testing
	public ProgramNode() {
		
	}
	
	
	// LinkedLists for all child nodes of a program
	LinkedList<BlockNode> BEGIN = new LinkedList<>();
	LinkedList<BlockNode> END = new LinkedList<>();
	LinkedList<BlockNode> OTHER = new LinkedList<>();
	LinkedList<FunctionDefinitionNode> FUNCTION = new LinkedList<>();
	
	
	
	// getter for FunctionDefinitionNode LinkedList
	public FunctionDefinitionNode getFUNCTION(){
		
		if(FUNCTION.isEmpty()) {
			return null;
		}
		
		else {
			return FUNCTION.pollLast();
		}
	}
	
	
	
	
	@Override
	public String toString() {
		
		
		String programNodeString = "";		// will hold string of the program
		
		// for each loop that iterates through LinkedList of functions and then concatenates them
		for (FunctionDefinitionNode i : FUNCTION) {
			programNodeString += i.toString() + "\n";
		}
		
		return programNodeString;
	}
	
	
	
}


// class for child node of program, block
class BlockNode extends Node{

	
	LinkedList<StatementNode> statements = new LinkedList<>();      // Linked List for child node of block, statements
	Optional<Node> condition;              							// Optional node that holds conditions inside block
	
	
	public BlockNode(LinkedList<StatementNode> statements, Optional<Node> condition) {
		
		this.statements = statements;
		this.condition = condition;
	}
	
	
	
	public LinkedList<StatementNode> getBlockStatements() {
		
		return statements;
	}
	
	
	
	@Override
	public String toString() {
		
		String statementsReturn = "";
		
		
		while (!statementsReturn.isEmpty()) {
			statementsReturn += statements.pop().toString();
			
			
		}
		
		
		
		
		return statementsReturn;
	}
	
}


// class for child node of program, function
class FunctionDefinitionNode extends Node{

	
	String functionName;				// hold name of function
	
	LinkedList<Optional<Token>> parameterNames = new LinkedList<>();			// holds parameters of function
	BlockNode block;					// holds blocknode inside of function
	
	
	// FunctionDefinitionNode class constructor
	public FunctionDefinitionNode(String functionName, LinkedList<Optional<Token>> parameterNames, BlockNode block) {	
		this.functionName = functionName;
		this.parameterNames = parameterNames;
		this.block = block;		
	}
	
	// constructor for BuiltinFunctionDefinitionNode
	public FunctionDefinitionNode(String functionName) {
		this.functionName = functionName;
	}
	
	
	public String getFunctionName() {
		return functionName;
	}
	
	
	@Override
	public String toString() {
		String nameFuncName = functionName;		// holds the string value of the function names
		String listOfParam = "";			// will hold all parameters
		
		// for each loop that concatenates all parameters together and puts a comma between them
		for (Optional<Token> i : parameterNames) {	
			listOfParam += i.orElseThrow().tokenValue + ", ";
		}
		
		// gets rid of trailing comma
		listOfParam = listOfParam.substring(0, listOfParam.length()-2);
		return "function " + nameFuncName + "(" + listOfParam + ")";
	}
}








// subclass of FuntionDefinitionNode for built in functions
class BuiltInFunctionDefinitionNode extends FunctionDefinitionNode{

	
	Function<HashMap<String, InterpreterDataType>, String> execute;		// Function variable
	
	public BuiltInFunctionDefinitionNode(String functionName, Function<HashMap<String, InterpreterDataType>, String> execute) {
		super(functionName);
		this.execute = execute;
		
		
	}
}






// node for operations
class OperationNode extends StatementNode{

	public Node left;					// variable to hold left node
	public Optional<Node> right = Optional.empty();		// variable to hold right node
	
	// different operations
	enum operations{EQ,NE,LT,LE,GT,GE,AND,OR,NOT,MATCH,NOTMATCH,DOLLAR,PREINC,POSTINC,
		PREDEC,POSTDEC,UNARYPOS,UNARYNEG,IN,EXPONENT,ADD,SUBTRACT,MULTIPLY,DIVIDE,MODULO,CONCATENATION}
	
	
	operations operation;		// variable to hold operation
	

	

	
	// class constructor for a node after operation (ex: x+y)
	public OperationNode(Node left, Optional<Node> right, OperationNode.operations operation) {
		
		this.left =left;
		this.right = right;
		this.operation = operation;
	}
	
	
	// class constructor for just a node folled by an operation (ex: x++ or $8)
	public OperationNode(Node left, OperationNode.operations operation) {
		
	
		this.left =left;
		this.operation = operation;
	}
	
	
	
	
	
	@Override
	public String toString() {
		
		String holdOpNode = "";
		
		
		// if there is not right node
		if (right.equals(Optional.empty())) {
			
			
			holdOpNode += operation.toString() + left.toString();
			
		}
		
		
		
		// if there is a right node
		else {
			holdOpNode += "(" + left.toString() + operation.toString() + right.toString() + ")";
		}
		
		return holdOpNode;
	}
	
}


// node for patterns
class PatternNode extends Node{

	private String name;		// holds String value of the node
		
		
	// class constructor
	public PatternNode(String name) {
		this.name = name;
	}
	
	
	public String getPattern() {return name;}
		
		
		
	@Override
	public String toString() {
		
		return "REGULAREXPRESSION" + name;
	}
		
		
		
}
	
	
	
// node for constants
class ConstantNode extends Node{

		
	private String name;		// holds String value of the node
		
		
	// class constructor
	public ConstantNode(String name) {
		this.name = name;
	}
		
		
		
	@Override
	public String toString() {
		
		return "NUMBER" + name;
	}
		
		
		
		
}
	
		



// node for variable references
class VariableReferenceNode extends Node{

	
	private String nameVariable;				// name of variable
	private Optional<Node> expressionIndex = Optional.empty();		// expression for the index
	
	// class constructor for array variables
	public VariableReferenceNode(String nameVariable, Optional<Node> expressionIndex) {
		this.nameVariable = nameVariable;
		this.expressionIndex = expressionIndex;
		
	}
	
	
	// class constructor for non array variables
	public VariableReferenceNode(String nameVariable) {
		this.nameVariable = nameVariable;
	}
	
	// returns name
	public String getName() {return nameVariable;}
	// returns index
	public Optional<Node> getIndex() {return expressionIndex;}
	
	
	
	@Override
	public String toString() {
		
		String varRef = "";
		
		// if variable is not for an array reference
		if(expressionIndex.equals(Optional.empty())) {
			
			varRef += nameVariable;
		}
		
		
		// else meaning variable is an array reference
		else {
			
			varRef += nameVariable + "[" + expressionIndex.toString() + "]";
		}
		
		
		return varRef;
	}
	
	
	
	
}



class TernaryNode extends Node{

	
	Node condition;
	Node trueCase;
	Node falseCase;
	
	
	public TernaryNode(Node condition, Node trueCase, Node falseCase) {
		
		this.condition = condition;
		this.trueCase = trueCase;
		this.falseCase = falseCase;
	}
	
	public Node getCondition() {
		return condition;
	}
	
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}




class AssignmentNode extends Node{

	
	Node target;
	Node expression;
	
	
	public AssignmentNode(Node target, Node expression) {
		this.target = target;
		this.expression = expression;
		
	}
	
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}




// abstract class for statements
abstract class StatementNode extends Node{
	
	
	
}



class ContinueNode extends StatementNode{

	
	
	
	@Override
	public String toString() {
		
		return "continue";
		
	}
	
}



class BreakNode extends StatementNode{

	
	
	
	@Override
	public String toString() {
		
		return "break";
	}
	
}



class IfNode extends StatementNode{

	Node condition;
	BlockNode statement;
	Optional<StatementNode> elseIf;
	
	
	
	public IfNode(Node condition, BlockNode statement, Optional<StatementNode> elseIf) {
		
		this.condition = condition;
		this.statement = statement;
		this.elseIf = elseIf;
	}
	
	
	@Override
	public String toString() {
		
		
		return "if";
	}
	
}


class ForNode extends StatementNode{

	Node startVar;
	Node condition;
	Node iteration;
	BlockNode body;
	
	public ForNode(Node startVar, Node condition, Node iteration, BlockNode body) {
		
		this.startVar = startVar;
		this.condition = condition;
		this.iteration = iteration;
		this.body = body;
		
	}
	
	
	
	
	
	
	@Override
	public String toString() {
		
		return "for";
	}
	
}




class ForEachNode extends StatementNode{

	Node loopVar;
	Node arrayVar;
	BlockNode body;
	
	public ForEachNode(Node loopVar, Node arrayVar, BlockNode body) {
		
		this.loopVar = loopVar;
		this.arrayVar = arrayVar;
		this.body = body;
		
	}
	
	
	
	
	
	
	@Override
	public String toString() {


		return "forEach";
	}
	
}





class DeleteNode extends StatementNode{

	
	VariableReferenceNode whatToDelete;
	
	public DeleteNode(Node delete) {
		if(delete instanceof VariableReferenceNode) {
			this.whatToDelete = (VariableReferenceNode)delete;
		}
	}
	
	
	
	
	@Override
	public String toString() {


		return "delete";
	}
	
}





class WhileNode extends StatementNode{

	Node condition;
	BlockNode statement;
	
	public WhileNode(Node condition, BlockNode statement) {
		
		this.condition = condition;
		this.statement = statement;
	}
	
	
	
	
	
	@Override
	public String toString() {


		return "while";
	}
	
}




class DoWhileNode extends StatementNode{

	
	Node condition;
	BlockNode statement;
	
	public DoWhileNode(Node condition, BlockNode statement) {
		
		this.condition = condition;
		this.statement = statement;
	}
	
	
	@Override
	public String toString() {
		
		return "doWhile";
	}
	
}



class ReturnNode extends StatementNode{

	Optional<Node> parseOp = Optional.empty();
	
public ReturnNode(Optional<Node> parseOp) {
		
		this.parseOp = parseOp;
	}
	
	
	@Override
	public String toString() {


		return "return";
	}
	
}



class ParseOperationNode extends StatementNode{

	Node parseOp;
	
public ParseOperationNode(Node parseOp) {
		
		this.parseOp = parseOp;
	}
	
	
	@Override
	public String toString() {
		
		// might be wrong *****
		return parseOp.toString();
	}
	
}



class FunctionCallNode extends StatementNode{

	String nameFunction;
	LinkedList<Node> parameters;
	
public FunctionCallNode(String nameFunction, LinkedList<Node> parameters) {
		
		this.nameFunction = nameFunction;
		this.parameters = parameters;
	}
	
	
	@Override
	public String toString() {


		return "functionCall";
	}
	
}


