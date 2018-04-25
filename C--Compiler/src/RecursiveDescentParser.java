import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/* ********************************************************
 * Name: Kyle Paxton 
 * Course: CSC 446
 * Assignment: Assignment 7
 * Date: 04/18/2018
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
 * STAT_LIST -> Statement ; STAT_LIST | ~
 * RET_STAT -> returnToken Expr ;
 * AssignStat -> idt = Expr | idt = FuncCall
 * Expr -> Relation
 * Relation -> SimpleExpr
 * SimpleExpr -> SignOp Term MoreTerm
 * MoreTerm -> Addop Term MoreTerm | ~
 * Factor -> id | num | ( Expr )
 * Addop -> + | - | '||' 
 * SignOp -> ! | - | ~
 * MulOp -> * | / | &&
 * FuncCall -> idt ( Params )
 * Params -> idt ParamsTail | num ParamsTail | ~
 * ParamsTail -> , idt ParamsTail | , num ParamsTail | ~
 **********************************************************/

public class RecursiveDescentParser {

	public static int depth = 1;

	public static int paramOffset = 4;
	public static int variableOffset = -2;
	public static int localSize = 0;
	public static String funcNameHold = "";
	public static String returnVariable = "";
	public static String sign = "";
	public static String outputString = "";
	public static int stringName = 0;
	
	public static ArrayList<String> items = new ArrayList<>();
	
	public static ArrayList<String> declaredVariables= new ArrayList<>();
	public static ArrayList<String> variables= new ArrayList<>();
	public static ArrayList<String> operations = new ArrayList<>();
	public static LexicalAnalyzer.Symbol funcReturnHold;
	static LexicalAnalyzer.Symbol currentReturnType;
	public static LinkedList<ParameterEntry> parameterList = new LinkedList<ParameterEntry>();
	public static Stack<String> paramStack = new Stack<>();
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
			symbolTable.insert(setParameterVariable(Globals.lexeme, currentReturnType, depth));
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
			symbolTable.insert(setParameterVariable(Globals.lexeme, currentReturnType, depth)); 
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
		outputTacLine("Proc "+ funcNameHold);
		variableOffset = -2;
		Match(LexicalAnalyzer.Symbol.leftBracketToken);
		Decl();
		StatList();
		RetStat();
		Match(LexicalAnalyzer.Symbol.rightBracketToken);
		outputTacLine("Endp "+ funcNameHold);
		
		depth--;
		symbolTable.insert(setFunction(funcNameHold, funcReturnHold, depth, parameterList.size(), localSize));
		localSize = 0;
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
		if(depth > 1) {			
			updateLocalSize(currentReturnType);
		}
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
			if(depth > 1) {			
				updateLocalSize(currentReturnType);
			}
			Match(LexicalAnalyzer.Symbol.identifierToken);
			IdTail();
		} else {
			// do nothing
		}
	}

	// STAT_LIST -> Statement ; STAT_LIST | ~
	public static void StatList() {
		if(Globals.token == LexicalAnalyzer.Symbol.identifierToken ||
			Globals.token == LexicalAnalyzer.Symbol.coutToken ||
			Globals.token == LexicalAnalyzer.Symbol.cinToken){			
			Statement();
			Match(LexicalAnalyzer.Symbol.semiColonToken);
			StatList();
		}
		else {
			//do nothing
		}
	}
	// Statement - > AssignStat | IOStat
	public static void Statement() {
		if(Globals.token == LexicalAnalyzer.Symbol.identifierToken  ) {
			if(symbolTable.lookUp(Globals.lexeme) != null) {
				AssignStat();				
			}
			else {
				System.out.println("Undeclared variable: " + Globals.lexeme + " at line number: " + Globals.lineNo);
				System.exit(0);
			}
		}
		else {
			IOStat();
		}
	}
	
	private static void IOStat() {
		if(Globals.token == LexicalAnalyzer.Symbol.cinToken ) {
			InStat();			
		}
		else {
			OutStat();			
		}
	}
	
	private static void InStat() {
		Match(LexicalAnalyzer.Symbol.cinToken);
		Match(LexicalAnalyzer.Symbol.rightShiftToken);
		outputTacLine(tacName("rdi " + tacName(Globals.lexeme)));
		Match(LexicalAnalyzer.Symbol.identifierToken);
		InEnd();
		
		
	}

	private static void InEnd() {
		if(Globals.token == LexicalAnalyzer.Symbol.rightShiftToken) {
			Match(LexicalAnalyzer.Symbol.rightShiftToken);
			Match(LexicalAnalyzer.Symbol.identifierToken);
			InEnd();
		}
		else {
			//do nothing
		}
	}
	
	private static void OutStat() {
		Match(LexicalAnalyzer.Symbol.coutToken);
		Match(LexicalAnalyzer.Symbol.leftShiftToken);		
		OutOptions();
		OutEnd();
		
	}
	
	private static void OutOptions() {
		if(Globals.token == LexicalAnalyzer.Symbol.identifierToken) {
			outputTacLine(tacName("wri " + tacName(Globals.lexeme)));
			Match(LexicalAnalyzer.Symbol.identifierToken);
		}
		else if(Globals.token == LexicalAnalyzer.Symbol.literalToken) {
			outputTacLine(tacName("wrs " + tacName(Globals.lexeme)));
			symbolTable.insert(setStringEntry(Globals.lexeme, LexicalAnalyzer.Symbol.literalToken, depth));
			updateLocalSize(LexicalAnalyzer.Symbol.literalToken);
			Match(LexicalAnalyzer.Symbol.literalToken);
		}
		else {
			outputTacLine("wrln");
			Match(LexicalAnalyzer.Symbol.endLineToken);
		}
	}
	
	private static void OutEnd() {
		if(Globals.token == LexicalAnalyzer.Symbol.leftShiftToken) {
			Match(LexicalAnalyzer.Symbol.leftShiftToken);
			OutOptions();
			OutEnd();
		}
		else {
			//do nothing
		}
	}

	
	//AssignStat -> idt = Expr | idt = FuncCall
	private static void AssignStat() {
		returnVariable = Globals.lexeme;
		
		BaseTableEntry exprTableEntry = new BaseTableEntry(null, null, depth);
		
		Match(LexicalAnalyzer.Symbol.identifierToken);
		Match(LexicalAnalyzer.Symbol.assignoptToken);
		if(Globals.character != '(') {
			//setAssignTacLine();
			exprTableEntry = Expr(exprTableEntry);
			outputTacLine(tacName(returnVariable) + " = " + tacName(exprTableEntry.lexeme));
		}
		else {
			FuncCall();
		}
		

	}

	//Expr -> Relation
	private static BaseTableEntry Expr(BaseTableEntry exprTableEntry) {
		exprTableEntry = Relation(exprTableEntry);
		
		return exprTableEntry;
	}

	// Relation -> SimpleExpr
	private static BaseTableEntry Relation(BaseTableEntry exprTableEntry) {
		exprTableEntry = SimpleExpr(exprTableEntry);
		return exprTableEntry;
	}

	//SimpleExpr -> SignOp Term MoreTerm
	private static BaseTableEntry SimpleExpr(BaseTableEntry exprTableEntry) {
		
		BaseTableEntry termTableEntry = new BaseTableEntry(null, null, depth);
		
		SignOp();
		termTableEntry = Term(termTableEntry);
		termTableEntry = MoreTerm(termTableEntry);
		exprTableEntry = termTableEntry;
		return exprTableEntry;
	}

	//MoreTerm -> Addop Term MoreTerm | ~
	private static BaseTableEntry MoreTerm(BaseTableEntry moreTermTableEntry) {
		BaseTableEntry myMoreTermTableEntry = new BaseTableEntry(null, null, depth);
		if (Globals.token == LexicalAnalyzer.Symbol.addoptToken ) {
			String operation = Globals.lexeme;
			String tempVariable = generateTempVariable();
			AddOp();	
			
			myMoreTermTableEntry = Term(myMoreTermTableEntry);
			outputTacLine(tempVariable + " = " + tacName(moreTermTableEntry.lexeme) +  " " + operation + " " + tacName(myMoreTermTableEntry.lexeme));
			
			moreTermTableEntry.lexeme = tempVariable;
			myMoreTermTableEntry = MoreTerm(myMoreTermTableEntry);
			
		}
		else {
			//do nothing
		}
		
		return moreTermTableEntry;
	}
	//Term -> Factor MoreFactor
	private static BaseTableEntry Term(BaseTableEntry termTableEntry) {
		
		BaseTableEntry factorTableEntry = new BaseTableEntry(null, null, depth);
		
		factorTableEntry = Factor(factorTableEntry);
		factorTableEntry = MoreFactor(factorTableEntry);
		termTableEntry = factorTableEntry;
		return termTableEntry;
	}
	//MoreFactor -> Mulop Factor MoreFactor | ~
	private static BaseTableEntry MoreFactor(BaseTableEntry morefactorTableEntry) {
		if (Globals.token == LexicalAnalyzer.Symbol.muloptToken ) {
			String operation = Globals.lexeme;
			String tempVariable = generateTempVariable();
			MulOp();
			
			BaseTableEntry myMoreFactorTableEntry = new BaseTableEntry(null, null, depth);
			
			myMoreFactorTableEntry = Factor(myMoreFactorTableEntry);
			outputTacLine(tempVariable + " = " + tacName(morefactorTableEntry.lexeme) +  " " + operation + " " + tacName(myMoreFactorTableEntry.lexeme));
			
			morefactorTableEntry.lexeme = tempVariable;
			myMoreFactorTableEntry = MoreFactor(myMoreFactorTableEntry);
			
		}
		else {
			//do nothing
		}
		return morefactorTableEntry;
	}

	//Factor -> id | num | ( Expr )
	private static BaseTableEntry Factor(BaseTableEntry factorTableEntry) {
		if (Globals.token == LexicalAnalyzer.Symbol.identifierToken ) {
			BaseTableEntry checkTableEntry = symbolTable.lookUp(Globals.lexeme);
			
			if(checkTableEntry instanceof ConstantEntry) {
				factorTableEntry.lexeme = tacName(checkTableEntry.lexeme);
			}
			else {
				factorTableEntry.lexeme = checkTableEntry.lexeme;
			}
			
			Match(LexicalAnalyzer.Symbol.identifierToken);
		}
		else if (Globals.token == LexicalAnalyzer.Symbol.numberToken ) {
			factorTableEntry.lexeme = tacName(Globals.lexeme);
			Match(LexicalAnalyzer.Symbol.numberToken);
		}
		else {
			Match(LexicalAnalyzer.Symbol.leftParenthesisToken);
			Expr(factorTableEntry);
			Match(LexicalAnalyzer.Symbol.rightParenthesisToken);
		}
		return factorTableEntry;
	}
	
	// Addop -> + | - | '||' 
	private static void AddOp() {
		Match(LexicalAnalyzer.Symbol.addoptToken);
	}
	
	//SignOp -> ! | - | ~
	private static void SignOp() {
		if (Globals.token == LexicalAnalyzer.Symbol.signoptToken ) {
			Match(LexicalAnalyzer.Symbol.signoptToken);
		}
		else if (Globals.lexeme.equals("-") ) {
			sign += Globals.lexeme;
			Match(LexicalAnalyzer.Symbol.addoptToken);
		}
		else {
			//do nothing
		}
	}
	
	// MulOp -> * | / | &&
	private static void MulOp() {
		Match(LexicalAnalyzer.Symbol.muloptToken);
	}
	
	// RET_STAT -> returnToken Expr ;
	public static void RetStat() {
		Match(LexicalAnalyzer.Symbol.returnToken);

		BaseTableEntry exprTableEntry = new BaseTableEntry(null, null, depth);
		exprTableEntry = Expr(exprTableEntry);
		outputTacLine("ax = " + tacName(exprTableEntry.lexeme));
		Match(LexicalAnalyzer.Symbol.semiColonToken);
		

	}

	// FuncCall -> idt ( Params )
	private static void FuncCall() {
		String funcName = Globals.lexeme;
		
		Match(LexicalAnalyzer.Symbol.identifierToken);
		Match(LexicalAnalyzer.Symbol.leftParenthesisToken);
		Params();
		Match(LexicalAnalyzer.Symbol.rightParenthesisToken);
		outputTacLine(tacName(funcName));
	}
	
	//Params -> idt ParamsTail | num ParamsTail | ~
	private static void Params() {			
		if (Globals.token == LexicalAnalyzer.Symbol.identifierToken) {
			paramStack.push(Globals.lexeme);
			Match(LexicalAnalyzer.Symbol.identifierToken);
			if(Globals.token == LexicalAnalyzer.Symbol.commaToken) {
				ParamsTail();				
			}
		}
		else if (Globals.token == LexicalAnalyzer.Symbol.numberToken){
			paramStack.push(Globals.lexeme);
			Match(LexicalAnalyzer.Symbol.numberToken);
			if(Globals.token == LexicalAnalyzer.Symbol.commaToken) {
				ParamsTail();				
			}
		}
		else {
			//do nothing
		}
	}
	
	//ParamsTail -> , idt ParamsTail | , num ParamsTail | ~
	private static void ParamsTail() {
		Match(LexicalAnalyzer.Symbol.commaToken);
		if (Globals.token == LexicalAnalyzer.Symbol.identifierToken) {
			paramStack.push(Globals.lexeme);
			Match(LexicalAnalyzer.Symbol.identifierToken);
			if(Globals.token == LexicalAnalyzer.Symbol.commaToken) {
				ParamsTail();				
			}
		}
		else if (Globals.token == LexicalAnalyzer.Symbol.numberToken){
			paramStack.push(Globals.lexeme);
			Match(LexicalAnalyzer.Symbol.numberToken);
			if(Globals.token == LexicalAnalyzer.Symbol.commaToken) {
				ParamsTail();				
			}
		}
		else {
			//do nothing
		}
	}


	public static void printError(LexicalAnalyzer.Symbol desired) {
		System.out.println("Expected " + desired + " at line number: " + Globals.lineNo);
		System.out.println("Token used: " + Globals.token);
		System.exit(0);
	}
	

	private static VariableEntry setVariable(String lexeme, LexicalAnalyzer.Symbol returnType, int depth) {
		VariableEntry myVariableEntry = new VariableEntry(lexeme, returnType, depth);

		myVariableEntry.setVariableType(returnType);
		myVariableEntry.setOffset(variableOffset);
		switch (myVariableEntry.getVariableType()) {
		case intToken:
			myVariableEntry.setSize(2);
			variableOffset -= 2;
			break;
		case floatToken:
			myVariableEntry.setSize(4);
			variableOffset -= 4;
			break;
		case charToken:
			myVariableEntry.setSize(1);
			variableOffset -= 1;
			break;
		default:
			break;
		}
		
		return myVariableEntry;
	}
	
	private static StringEntry setStringEntry(String lexeme, LexicalAnalyzer.Symbol returnType, int depth) {
		StringEntry myStringEntry = new StringEntry(lexeme, returnType, depth);
		myStringEntry.setValue(Globals.literal);
		myStringEntry.setName("_S" + stringName);
		stringName++;
		return myStringEntry;
	}
	
	private static VariableEntry setParameterVariable(String lexeme, LexicalAnalyzer.Symbol returnType, int depth) {
		VariableEntry myVariableEntry = new VariableEntry(lexeme, returnType, depth);

		myVariableEntry.setVariableType(returnType);
		myVariableEntry.setOffset(paramOffset);
		switch (myVariableEntry.getVariableType()) {
		case intToken:
			myVariableEntry.setSize(2);
			paramOffset += 2;
			break;
		case floatToken:
			myVariableEntry.setSize(4);
			paramOffset += 4;
			break;
		case charToken:
			myVariableEntry.setSize(1);
			paramOffset += 1;
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
		
		//while(!parameterList.isEmpty()) {
		//	symbolTable.insert(parameterList.removeFirst());
		//}	
		
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
	
	//sets the TAC code on the AssignStat function
	
	public static void outputTacLine(String line) {
		System.out.println(line);
		Main.writer.println(line);
	}
	
	private static String tacName(String lexeme) {
		if(lexeme != null) {
			if(isInt(lexeme)) {
				
				String tempVariable = generateTempVariable();
				outputTacLine(tempVariable + " = " + sign + lexeme);
				sign = "";
				return tempVariable;
			}
			
			if(lexeme.startsWith("\"")){
				return "_S" + stringName;
				
			}
			
			BaseTableEntry checkTableEntry = symbolTable.lookUp(lexeme);
			if(checkTableEntry instanceof VariableEntry) {
				if(checkTableEntry.depth == 1) {
					return lexeme;
				}
				else {
					return "_bp" + ((VariableEntry)checkTableEntry).getOffset();
				}
			}
			else if(checkTableEntry instanceof FunctionEntry) {
				while(paramStack.size() != 0) {
					outputTacLine("Push " + tacName(paramStack.pop()));
				}
				outputTacLine("call " + lexeme);
				
				return tacName(returnVariable) + " = ax"; 
			}
			else if(checkTableEntry instanceof ConstantEntry) {
				return ((ConstantEntry)checkTableEntry).getValue().toString();
			}
			else {
				return lexeme;
				
			}
			
		}
		else {
			return lexeme;
		}
	}
	
	private static String generateTempVariable() {
		int oldOffset = variableOffset;
		variableOffset -= 2;
		return "_bp" + oldOffset;
	}
	

	
	static boolean isInt(String s)
	{
	 try
	  { int i = Integer.parseInt(s); return true; }

	 catch(NumberFormatException er)
	  { return false; }
	}
		

}
