import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;

/* ********************************************************
 * Name: Kyle Paxton 
 * Course: CSC 446
 * Assignment: Assignment 3
 * Date: 02/21/2018
 **********************************************************/

public class LexicalAnalyzer {
	
	public enum Symbol {beginToken, ifToken, elseToken, whileToken, intToken, floatToken, charToken, 
		leftSqrightBracketTokenoken, rightSqrightBracketTokenoken, leftBracketToken, rightBracketToken, leftParenthesisToken, rightParenthesisToken,
		quotationToken, commaToken, semiColonToken, periodToken, underscoreToken,
		numberToken, identifierToken, eofToken, unknownToken, relopToken, literalToken, 
		addoptToken, muloptToken, assignoptToken, signoptToken,
		breakToken, continueToken, voidToken, constToken, returnToken};
		
	static boolean comment = false;
	static String endOfLine = System.getProperty("line.separator");
	
	//retrieves the next token in the string and initiates processing
	static public void GetNextToken() {
		Globals.token = Symbol.unknownToken;
		while(Globals.character <= ' ' && Main.fileContents.length() > 0) {
			GetNextCharacter();
		}
		
		if(Main.fileContents.trim().isEmpty() && Globals.character == ' ') {
			Globals.token = LexicalAnalyzer.Symbol.eofToken;
		}
		else {
			ProcessToken();
		}
		
	}
	
	static public void GetNextCharacter() {
		if(Main.fileContents.length() > 0)
		{
			Globals.character = Main.fileContents.charAt(0);
			Main.fileContents = Main.fileContents.substring(1, Main.fileContents.length());
			
			if(Globals.character == endOfLine.charAt(0))
			{
				Globals.lineNo++;
				Globals.character = ' ';
			}
		}
		else {
			Globals.character = ' ';
		}
		
		
	}
	
	//checks the value of the current character and processes accordingly.
	static public void ProcessToken() {
		Globals.lexeme = String.valueOf(Globals.character);
		GetNextCharacter();
		
		if(IsWord(Globals.lexeme.charAt(0)))
		{
			ProcessWordToken();
		}
		else if(IsNum(Globals.lexeme.charAt(0)))
		{
			ProcessnumberTokenoken();
		}
		else if(IsDoubleSymbol(Globals.lexeme.charAt(0)))
		{
			ProcessDoubleToken();
		}
		else if(Globals.lexeme.charAt(0) == '/' && Globals.character == '*')
		{
			ProcessComment();
		}
		else if(Globals.lexeme.charAt(0) == '"')
		{
			ProcessLiteral();
		}
		else {
			ProcessSingleToken();
		}
	}
	
	//assigns the token if the item is a reserved word
	//if, else, while, int, float, char, break, continue, void
	static public void ProcessWordToken()
	{
		FillLexemeWord();
		for (Symbol sym : Symbol.values()) {
			if(sym == Main.resWord.get(Globals.lexeme))
			{
				Globals.token = sym;
				return;
			}
			else if(Globals.lexeme.length() > 27)
			{
				Globals.token = Symbol.unknownToken;
			}
			else{
				Globals.token = Symbol.identifierToken;
			}
		}			
		
	}
	
	//assigns the token if the item is a number value
	//1, 2, 3...
	static public void ProcessnumberTokenoken()
	{
		Globals.token = Symbol.numberToken;

		FillLexemeNumber();
		
		if(Globals.lexeme.indexOf('.') >= 0)
		{
			Globals.valueR = Float.parseFloat(Globals.lexeme);
		}
		else {
			Globals.value = Integer.parseInt(Globals.lexeme);
		}
	}
	
	//assigns the token if the item is a comment symbol
	// /*     */
	static public void ProcessComment()
	{
		comment = true; 
		DeleteComment();
		ClearLexeme();
	}
	
	//assigns the token if the item is a single symbol
	// {  }  [  ] ...
	static public void ProcessSingleToken()
	{
		for (Symbol sym : Symbol.values()) {
			if(sym == Main.characterSymbols.get(Globals.lexeme))
			{
				Globals.token = sym;
				return;
			}
		}
	}
	
	//assigns the token if the item is two tokens
	// ==, ||, && ...
	static public void ProcessDoubleToken()
	{
		FillLexemeDoubleSymbol();
		for (Symbol sym : Symbol.values()) {
			if(sym == Main.characterSymbols.get(Globals.lexeme))
			{
				Globals.token = sym;
				return;
			}
		}
	}
	
	//assigns the token if the item is a literal value
	// " ...
	static public void ProcessLiteral() 
	{

		Globals.token = Symbol.literalToken;
		FillLexemeLiteral();
		
		if(Globals.token == Symbol.literalToken)
		{
			Globals.literal = Globals.lexeme;
		}
		
	}
	
	static public void FillLexemeWord()
	{
		while(IsWord(Globals.character) || IsNum(Globals.character) || Globals.character == '_')
		{
			Globals.lexeme += Globals.character;
			GetNextCharacter();
		}
	}
	
	static public void FillLexemeNumber()
	{
		while(IsNum(Globals.character))
		{
			Globals.lexeme += Globals.character;
			GetNextCharacter();
		}
		
		if(Globals.character == '.' && IsNum(CheckNextChar()))
		{
			Globals.lexeme += Globals.character;
			GetNextCharacter();
			
			while(IsNum(Globals.character))
			{
				Globals.lexeme += Globals.character;
				GetNextCharacter();
			}
		}
	}
	
	static public void FillLexemeLiteral()
	{
		if(FindMatchingQuote())
		{
			while(Globals.character != '"')
			{
				if(Globals.character == endOfLine.charAt(0))
				{
					Globals.token = Symbol.quotationToken;
					return;
				}
				
				Globals.lexeme += Globals.character;
				GetNextCharacter();
			}
			
			Globals.lexeme += Globals.character;
			GetNextCharacter();
		}
		else
		{
			Globals.token = Symbol.quotationToken;
		}
		
		
	}
	
	static public void FillLexemeDoubleSymbol()
	{
		Globals.lexeme += Globals.character;
		GetNextCharacter();
	}
	
	static public boolean IsWord(char data) {
		if((data >= 'A' && data <='Z') || (data >= 'a' && data <='z'))
		{
			return true;
		}
		
		return false;
	}
	
	static public boolean IsNum(char data) {
		if(data >= '0' && data <='9')
		{
			return true;
		}
		
		return false;
	}

	static public boolean IsDoubleSymbol(char data) {
		String testString = "";
		testString += data;
		testString += Globals.character;
		if(testString.equals("==") || testString.equals("<=") || testString.equals("||") ||
			testString.equals("!=") || testString.equals(">=") || testString.equals("&&"))
		{
			return true;
		}
		return false;
	}
	
	static public boolean FindMatchingQuote()
	{
		String testString = Main.fileContents ;
		if(Main.fileContents.contains(endOfLine))
		{
			testString = Main.fileContents.substring(0, Main.fileContents.indexOf(endOfLine));
		}
		if(testString.indexOf('"') >= 0)
		{
			return true;
		}
		else {
			return false;
		}
	}
	
	static public void ClearLexeme() {
		Globals.lexeme = "";
		Globals.value = null;
		Globals.valueR = null;
		Globals.literal = null;
		
	}

	static public void DisplayToken()
	{
		if(Globals.value != null)
		{
			System.out.printf("%-30.30s  %-30.30s  %-30.30s%n" , Globals.lexeme, Globals.token, Globals.value);
		}
		else if(Globals.valueR != null) 
		{
			System.out.printf("%-30.30s  %-30.30s  %-30.30s%n" , Globals.lexeme, Globals.token, Globals.valueR);
		}
		else if(Globals.literal != null) 
		{
			System.out.printf("%-30.30s  %-30.30s  %-30.30s%n" , Globals.lexeme, Globals.token, Globals.literal);
		}
		else if(comment)
		{
			//do not print
			comment = false;
		}
		else 
		{
			System.out.printf("%-30.30s  %-30.30s  %-30.30s%n" , Globals.lexeme, Globals.token, "");
		}
	}
	
	static public void DisplayHeader()
	{
		System.out.println("**************************************************************************");
		System.out.printf("%-30.30s  %-30.30s  %-30.30s%n" , "lexeme", "token", "attribute");
		System.out.println("**************************************************************************");
	}
	
	static public char CheckNextChar() {
		return Main.fileContents.charAt(0);
	}

	//removes the comment from the fileContents
	static public void DeleteComment() {
		boolean continueLoop = true;
		while(continueLoop) {
			if(Main.fileContents.length() > 0)
			{
				Globals.character = Main.fileContents.charAt(0);
				Main.fileContents = Main.fileContents.substring(1, Main.fileContents.length());
			}
			else{
				System.out.println("Error!! - comment not closed");
				continueLoop = false;
			}
			
			if(Globals.character == '*' && Main.fileContents.charAt(0) == '/')
			{
				continueLoop = false;
				Globals.character = ' ';
				Main.fileContents = Main.fileContents.substring(1, Main.fileContents.length());
				GetNextToken();
			}
		}
	}
	
}
