
/* ********************************************************
 * Name: Kyle Paxton 
 * Course: CSC 446
 * Assignment: Assignment 3
 * Date: 02/21/2018
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

	// compares the current token with the expected. 
	// if equal retrieve next token
	// else error
	public static void Match(LexicalAnalyzer.Symbol desired) {
		
		if(Globals.token == desired){
			LexicalAnalyzer.GetNextToken();
		}
		else {
			printError(desired);
		}
		
	}
	
	//PROG -> TYPE idt REST PROG | ~
	public static void Prog() {
		if(Globals.token == LexicalAnalyzer.Symbol.intToken ||
			Globals.token == LexicalAnalyzer.Symbol.floatToken ||
			Globals.token == LexicalAnalyzer.Symbol.charToken) {
				Type();
				Match(LexicalAnalyzer.Symbol.identifierToken);
				Rest();
				Prog();
		}
		else {
			//do nothing
		}
	}
	
	//TYPE -> int | float| char
	public static void Type() {
		switch(Globals.token) {
			case intToken:
				Match(LexicalAnalyzer.Symbol.intToken);
				break;
			case floatToken:
				Match(LexicalAnalyzer.Symbol.floatToken);
				break;
			case charToken:
				Match(LexicalAnalyzer.Symbol.charToken);
				break;
			default:
				System.out.println("Invalid type at line number: " + Globals.lineNo);
				System.out.println("Token used: " + Globals.token);
				System.exit(0);
				break;	
		}
			
	}
	
	//REST -> ( PARAMLIST ) COMPOUND | IDTAIL ; PROG
	public static void Rest() {
		switch(Globals.token) {
		case leftParenthesisToken:
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
	

	//PARAMLIST -> TYPE idt PARAMTAIL | ~
	public static void ParamList() {
		if(Globals.token == LexicalAnalyzer.Symbol.intToken ||
		Globals.token == LexicalAnalyzer.Symbol.floatToken ||
		Globals.token == LexicalAnalyzer.Symbol.charToken) {
				Type();
				Match(LexicalAnalyzer.Symbol.identifierToken);
				ParamTail();
		}
		else
		{
			//do nothing
		}
	}
	
	//PARAMTAIL -> , TYPE idt PARAMTAIL | ~
	public static void ParamTail() {
		if(Globals.token == LexicalAnalyzer.Symbol.commaToken){
			Match(LexicalAnalyzer.Symbol.commaToken);
			Type();
			Match(LexicalAnalyzer.Symbol.identifierToken);
			ParamTail();
		}
		else {
			//do nothing
		}
	}
	
	//COMPOUND -> { DECL STAT_LIST RET_STAT }
	public static void Compound() {
		Match(LexicalAnalyzer.Symbol.leftBracketToken);
		Decl();
		StatList();
		RetStat();
		Match(LexicalAnalyzer.Symbol.rightBracketToken);
		
	}
	
	//DECL -> TYPE IDLIST | ~
	public static void Decl() {
		if(Globals.token == LexicalAnalyzer.Symbol.intToken ||
		Globals.token == LexicalAnalyzer.Symbol.floatToken ||
		Globals.token == LexicalAnalyzer.Symbol.charToken) {
			Type();
			IdList();
		}
		else {
			//do nothing
		}
		
	}
	
	//IDLIST -> idt IDTAIL ; DECL
	public static void IdList() {
		Match(LexicalAnalyzer.Symbol.identifierToken);
		IdTail();
		Match(LexicalAnalyzer.Symbol.semiColonToken);
		Decl();
	}
	
	//IDTAIL -> , idt IDTAIL | ~
	public static void IdTail() {
		if(Globals.token == LexicalAnalyzer.Symbol.commaToken){
			Match(LexicalAnalyzer.Symbol.commaToken);
			Match(LexicalAnalyzer.Symbol.identifierToken);
			IdTail();
		}
		else {
			//do nothing
		}
	}
	
	//STAT_LIST -> ~
	public static void StatList() {
		//do nothing
	}
	
	//RET_STAT  -> ~
	public static void RetStat() {
		//do nothing
	}
	
	public static void printError(LexicalAnalyzer.Symbol desired) {
		System.out.println("Expected " + desired + " at line number: " + Globals.lineNo);
		System.out.println("Token used: " + Globals.token);
		System.exit(0);
	}
}
