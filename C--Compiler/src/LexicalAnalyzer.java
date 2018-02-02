
import java.awt.DisplayMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Arrays;

import javax.lang.model.element.VariableElement;
import javax.security.auth.Subject;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import org.omg.CORBA.portable.ValueBase;
public class LexicalAnalyzer {
	
	public enum Symbol {begint, ift, elset, whilet, intt, floatt, chart, 
		lsqrbrackett, rsqrbrackett, lbrackett, rbrackett, lparent, rparent,
		quotationt, commat, semit, periodt, underscoret,
		numt, idt, eoft, unknownt, relopt, literalt, 
		addopt, mulopt, assignopt,
		breakt, continuet, voidt};
		
	static boolean comment = false;
	static String endOfLine = System.getProperty("line.separator");
	static public void GetNextToken() {
		Globals.token = Symbol.unknownt;
		while(Globals.character <= ' ') {
			GetNextCharacter();
		}
		if(Main.fileContents.isEmpty()) {
			Globals.token = LexicalAnalyzer.Symbol.eoft;
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
		}
		
	}
	
	static public void ProcessToken() {
		Globals.lexeme = String.valueOf(Globals.character);
		GetNextCharacter();
		
		if(IsWord(Globals.lexeme.charAt(0)))
		{
			ProcessWordToken();
		}
		else if(IsNum(Globals.lexeme.charAt(0)))
		{
			ProcessNumToken();
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
				Globals.token = Symbol.unknownt;
			}
			else{
				Globals.token = Symbol.idt;
			}
		}			
		
	}
	
	static public void ProcessNumToken()
	{
		Globals.token = Symbol.numt;

		FillLexemeNumber();
		
		if(Globals.lexeme.indexOf('.') >= 0)
		{
			Globals.valueR = Float.parseFloat(Globals.lexeme);
		}
		else {
			Globals.value = Integer.parseInt(Globals.lexeme);
		}
	}
	
	static public void ProcessComment()
	{
		comment = true; 
		DeleteComment();
		ClearLexeme();
	}
	
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
	
	static public void ProcessLiteral() 
	{

		Globals.token = Symbol.literalt;
		FillLexemeLiteral();
		
		if(Globals.token == Symbol.literalt)
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
					Globals.token = Symbol.quotationt;
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
			Globals.token = Symbol.quotationt;
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
			}
		}
	}
}
