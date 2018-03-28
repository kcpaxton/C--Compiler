import java.awt.List;
import java.util.LinkedList;

/* ********************************************************
 * Name: Kyle Paxton 
 * Course: CSC 446
 * Assignment: Assignment 5
 * Date: 03/28/2018
 **********************************************************/

/*Recursive Descent Parser designed with the grammar below*
 * 
 * PROG      -> TYPE idt REST PROG | ~
 * TYPE      -> int | float| char
 * REST      -> ( PARAMLIST ) COMPOUND | IDTAIL ; PROG
 * PARAMLIST -> TYPE idt PARAMTAIL | ~
 * PARAMTAIL -> , TYPE idt PARAMTAIL | ~
 * COMPOUND  -> { DECL STAT_LIST RET_STAT }
 * DECL      -> TYPE IDLIST | ~
 * IDLIST    -> idt IDTAIL ; DECL
 * IDTAIL    -> , idt IDTAIL | ~
 * STAT_LIST -> ~
 * RET_STAT  -> ~
 * 
 **********************************************************/

public class RecursiveDescentParser {

	public static int depth = 1;
	public static int offset = 0;
	public static int localSize = 0;
	public static String funcNameHold = "";
	public static LexicalAnalyzer.Symbol funcReturnHold;
	static LexicalAnalyzer.Symbol currentReturnType;
	public static LinkedList<ParameterEntry> parameterList = new LinkedList();

	static SymbolTable symbolTable = new SymbolTable();

	// compares the current token with the expected.
	// if equal retrieve next token
	// else error
	public static void Match(LexicalAnalyzer.Symbol desired) {

		if (Globals.token == desired) {
			LexicalAnalyzer.GetNextToken();
		} else {
			printError(desired);
		}

	}

	// PROG -> TYPE idt REST PROG |
	// const idt = num ; PROG |
	// ~
	public static void Prog() {
		if (Globals.token == LexicalAnalyzer.Symbol.intToken 
				|| Globals.token == LexicalAnalyzer.Symbol.floatToken
				|| Globals.token == LexicalAnalyzer.Symbol.charToken) {
			Type();
			if (Globals.character == ';' || Globals.character == ',') {
				symbolTable.insert(setVariable(Globals.lexeme, currentReturnType, depth));
			}
			else if(Globals.character == '(') {
				funcNameHold = Globals.lexeme;
				funcReturnHold = currentReturnType;
			}
			Match(LexicalAnalyzer.Symbol.identifierToken);
			Rest();
			Prog();
		} else if (Globals.token == LexicalAnalyzer.Symbol.constToken) {
			Match(LexicalAnalyzer.Symbol.constToken);
			//hold the identifier name 
			String tempName = Globals.lexeme;
			Match(LexicalAnalyzer.Symbol.identifierToken);
			Match(LexicalAnalyzer.Symbol.assignoptToken);
			symbolTable.insert(setConst(tempName, depth));
			Match(LexicalAnalyzer.Symbol.numberToken);
			Match(LexicalAnalyzer.Symbol.semiColonToken);
			Prog();
		} else {
			// do nothing
		}
		
		
	}

	// TYPE -> int | float| char
	public static void Type() {
		switch (Globals.token) {
		case intToken:
			currentReturnType = LexicalAnalyzer.Symbol.intToken;
			Match(LexicalAnalyzer.Symbol.intToken);
			break;
		case floatToken:
			currentReturnType = LexicalAnalyzer.Symbol.floatToken;
			Match(LexicalAnalyzer.Symbol.floatToken);
			break;
		case charToken:
			currentReturnType = LexicalAnalyzer.Symbol.charToken;
			Match(LexicalAnalyzer.Symbol.charToken);
			break;
		default:
			System.out.println("Invalid type at line number: " + Globals.lineNo);
			System.out.println("Token used: " + Globals.token);
			System.exit(0);
			break;
		}

	}

	// REST -> ( PARAMLIST ) COMPOUND | IDTAIL ; PROG
	public static void Rest() {
		switch (Globals.token) {
		case leftParenthesisToken:
			offset = 0;
			depth++;
			Match(LexicalAnalyzer.Symbol.leftParenthesisToken);
			ParamList();
			Match(LexicalAnalyzer.Symbol.rightParenthesisToken);
			Compound();
			break;
		default:
			IdTail();
			Match(LexicalAnalyzer.Symbol.semiColonToken);
			Prog();
			break;
		}
	}

	// PARAMLIST -> TYPE idt PARAMTAIL | ~
	public static void ParamList() {
		if (Globals.token == LexicalAnalyzer.Symbol.intToken || Globals.token == LexicalAnalyzer.Symbol.floatToken
				|| Globals.token == LexicalAnalyzer.Symbol.charToken) {
			Type();
			symbolTable.insert(setVariable(Globals.lexeme, currentReturnType, depth));
			parameterList.add(setParameter(Globals.lexeme, currentReturnType, depth));
			updateLocalSize(currentReturnType);

			Match(LexicalAnalyzer.Symbol.identifierToken);
			ParamTail();
		} else {
			// do nothing
		}
	}

	// PARAMTAIL -> , TYPE idt PARAMTAIL | ~
	public static void ParamTail() {
		if (Globals.token == LexicalAnalyzer.Symbol.commaToken) {
			Match(LexicalAnalyzer.Symbol.commaToken);
			Type();
			symbolTable.insert(setVariable(Globals.lexeme, currentReturnType, depth)); 
			parameterList.add(setParameter(Globals.lexeme, currentReturnType, depth));
			updateLocalSize(currentReturnType);
			Match(LexicalAnalyzer.Symbol.identifierToken);
			ParamTail();
		} else {
			// do nothing
		}
	}

	// COMPOUND -> { DECL STAT_LIST RET_STAT }
	public static void Compound() {
		Match(LexicalAnalyzer.Symbol.leftBracketToken);
		offset = 0;
		Decl();
		StatList();
		RetStat();
		Match(LexicalAnalyzer.Symbol.rightBracketToken);		
		symbolTable.insert(setFunction(funcNameHold, funcReturnHold, depth-1, parameterList.size(), localSize));
		symbolTable.writeTable(depth);
		localSize = 0;
		depth--;
		parameterList.clear();
	}

	// DECL -> TYPE IDLIST
	// const idt = num ; DECL |
	// ~
	public static void Decl() {
		if (Globals.token == LexicalAnalyzer.Symbol.intToken || Globals.token == LexicalAnalyzer.Symbol.floatToken
				|| Globals.token == LexicalAnalyzer.Symbol.charToken) {
			Type();

			IdList();
		} else if (Globals.token == LexicalAnalyzer.Symbol.constToken) {
			Match(LexicalAnalyzer.Symbol.constToken);
			//hold the identifier name 
			String tempName = Globals.lexeme;
			Match(LexicalAnalyzer.Symbol.identifierToken);
			Match(LexicalAnalyzer.Symbol.assignoptToken);
			symbolTable.insert(setConst(tempName, depth));
			Match(LexicalAnalyzer.Symbol.numberToken);
			Match(LexicalAnalyzer.Symbol.semiColonToken);
			Decl();
		} else {
			// do nothing
		}

	}

	// IDLIST -> idt IDTAIL ; DECL
	public static void IdList() {
		symbolTable.insert(setVariable(Globals.lexeme, currentReturnType, depth));
		updateLocalSize(currentReturnType);
		Match(LexicalAnalyzer.Symbol.identifierToken);
		IdTail();
		Match(LexicalAnalyzer.Symbol.semiColonToken);
		Decl();
	}

	// IDTAIL -> , idt IDTAIL | ~
	public static void IdTail() {
		if (Globals.token == LexicalAnalyzer.Symbol.commaToken) {
			Match(LexicalAnalyzer.Symbol.commaToken);
			symbolTable.insert(setVariable(Globals.lexeme, currentReturnType, depth));
			Match(LexicalAnalyzer.Symbol.identifierToken);
			IdTail();
		} else {
			// do nothing
		}
	}

	// STAT_LIST -> ~
	public static void StatList() {
		// do nothing
	}

	// RET_STAT -> ~
	public static void RetStat() {
		// do nothing
	}

	public static void printError(LexicalAnalyzer.Symbol desired) {
		System.out.println("Expected " + desired + " at line number: " + Globals.lineNo);
		System.out.println("Token used: " + Globals.token);
		System.exit(0);
	}

	private static VariableEntry setVariable(String lexeme, LexicalAnalyzer.Symbol returnType, int depth) {
		VariableEntry myVariableEntry = new VariableEntry(lexeme, returnType, depth);

		myVariableEntry.setVariableType(returnType);
		myVariableEntry.setOffset(offset);
		switch (myVariableEntry.getVariableType()) {
		case intToken:
			myVariableEntry.setSize(2);
			offset += 2;
			break;
		case floatToken:
			myVariableEntry.setSize(4);
			offset += 4;
			break;
		case charToken:
			myVariableEntry.setSize(1);
			offset += 1;
			break;
		default:
			break;
		}

		return myVariableEntry;
	}

	private static ConstantEntry setConst(String lexeme, int depth) {
		ConstantEntry myConstEntry = new ConstantEntry(lexeme, LexicalAnalyzer.Symbol.constToken, depth);
		if (Globals.valueR != null) {
			myConstEntry.setValueReal(Globals.valueR);
			Globals.valueR = null;
		} else if(Globals.value != null){
			myConstEntry.setValue(Globals.value);
			Globals.value = null;
		}

		return myConstEntry;
	}
	
	private static FunctionEntry setFunction(String lexeme, LexicalAnalyzer.Symbol returnType, int depth, int parameterListCount, int localSize) {
		FunctionEntry myFunctionEntry = new FunctionEntry(lexeme, returnType, depth);
		myFunctionEntry.setLocalSize(localSize);
		myFunctionEntry.setParameterCount(parameterListCount);
		
		while(!parameterList.isEmpty()) {
			symbolTable.insert(parameterList.removeFirst());
		}	
		
		return myFunctionEntry;
	}
	private static ParameterEntry setParameter(String lexeme, LexicalAnalyzer.Symbol returnType, int depth) {
		ParameterEntry myParameterEntry = new ParameterEntry(lexeme, returnType, depth);
		
		return myParameterEntry;
	}
	private static void updateLocalSize(LexicalAnalyzer.Symbol returnType) {
		switch (returnType) {
		case intToken:
			localSize += 2;
			break;
		case floatToken:
			localSize += 4;
			break;
		case charToken:
			localSize += 1;
			break;
		default:
			break;
		}
	}
}
