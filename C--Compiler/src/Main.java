/* ********************************************************
 * Name: Kyle Paxton 
 * Course: CSC 446
 * Assignment: Assignment 7
 * Date: 04/18/2018
 **********************************************************/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Paths;

import javax.imageio.ImageTypeSpecifier;

import org.omg.CORBA.PUBLIC_MEMBER;




public class Main {
	static String fileContents = "";
	static Scanner scanner;
	static PrintWriter writer = null;
	static String fileName = null;
	final static HashMap<String, LexicalAnalyzer.Symbol> resWord = new HashMap<String, LexicalAnalyzer.Symbol>();
	final static HashMap<String, LexicalAnalyzer.Symbol> characterSymbols = new HashMap<String, LexicalAnalyzer.Symbol>();
	
	public static void main(String[] args) {
		
		Globals.token = LexicalAnalyzer.Symbol.unknownToken;

		resWord.put("if", LexicalAnalyzer.Symbol.ifToken);
		resWord.put("else", LexicalAnalyzer.Symbol.elseToken);
		resWord.put("while", LexicalAnalyzer.Symbol.whileToken);
		resWord.put("int", LexicalAnalyzer.Symbol.intToken);
		resWord.put("float", LexicalAnalyzer.Symbol.floatToken);
		resWord.put("char", LexicalAnalyzer.Symbol.charToken);
		resWord.put("break", LexicalAnalyzer.Symbol.breakToken);
		resWord.put("continue", LexicalAnalyzer.Symbol.continueToken);
		resWord.put("void", LexicalAnalyzer.Symbol.voidToken);
		resWord.put("const", LexicalAnalyzer.Symbol.constToken);
		resWord.put("return", LexicalAnalyzer.Symbol.returnToken);
		resWord.put("cout", LexicalAnalyzer.Symbol.coutToken);
		resWord.put("cin", LexicalAnalyzer.Symbol.cinToken);
		resWord.put("endl", LexicalAnalyzer.Symbol.endLineToken);
		
		characterSymbols.put("{", LexicalAnalyzer.Symbol.leftBracketToken);
		characterSymbols.put("}", LexicalAnalyzer.Symbol.rightBracketToken);
		characterSymbols.put("[", LexicalAnalyzer.Symbol.leftSqrightBracketTokenoken);
		characterSymbols.put("]", LexicalAnalyzer.Symbol.rightSqrightBracketTokenoken);
		characterSymbols.put("(", LexicalAnalyzer.Symbol.leftParenthesisToken);
		characterSymbols.put(")", LexicalAnalyzer.Symbol.rightParenthesisToken);
		characterSymbols.put("\"",LexicalAnalyzer.Symbol.quotationToken );
		characterSymbols.put(",", LexicalAnalyzer.Symbol.commaToken);
		characterSymbols.put(";", LexicalAnalyzer.Symbol.semiColonToken);
		characterSymbols.put(".", LexicalAnalyzer.Symbol.periodToken);
		characterSymbols.put("_", LexicalAnalyzer.Symbol.underscoreToken);
		characterSymbols.put( "==", LexicalAnalyzer.Symbol.relopToken);
		characterSymbols.put("!=", LexicalAnalyzer.Symbol.relopToken);
		characterSymbols.put("<", LexicalAnalyzer.Symbol.relopToken);
		characterSymbols.put("<=", LexicalAnalyzer.Symbol.relopToken);
		characterSymbols.put(">", LexicalAnalyzer.Symbol.relopToken);
		characterSymbols.put(">=", LexicalAnalyzer.Symbol.relopToken);
		characterSymbols.put("+", LexicalAnalyzer.Symbol.addoptToken);
		characterSymbols.put("-", LexicalAnalyzer.Symbol.addoptToken);
		characterSymbols.put("||", LexicalAnalyzer.Symbol.addoptToken);
		characterSymbols.put("&&", LexicalAnalyzer.Symbol.muloptToken);
		characterSymbols.put("/", LexicalAnalyzer.Symbol.muloptToken);
		characterSymbols.put("%", LexicalAnalyzer.Symbol.muloptToken);
		characterSymbols.put("*", LexicalAnalyzer.Symbol.muloptToken);
		characterSymbols.put("!", LexicalAnalyzer.Symbol.signoptToken);
		characterSymbols.put("=", LexicalAnalyzer.Symbol.assignoptToken);
		characterSymbols.put("<<", LexicalAnalyzer.Symbol.leftShiftToken);
		characterSymbols.put(">>", LexicalAnalyzer.Symbol.rightShiftToken);
		
        
		try {
			fileContents = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			fileName =args[0].substring(0, args[0].indexOf("."));
			writer = new PrintWriter(fileName+".tac",  "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		LexicalAnalyzer.GetNextToken();
		RecursiveDescentParser.Prog();
		
		
		if(Globals.token != LexicalAnalyzer.Symbol.eofToken)
		{
			System.out.println("ERROR - Unused Tokens!");
			System.exit(0);
			
		}
		checkMain();
		writer.close();
		System.out.println("");
		System.out.println("Successfully wrote to: " + fileName + ".tac");
		System.out.println("");
		
		CodeGenerator codeGenerator = new CodeGenerator(fileName+".tac", RecursiveDescentParser.symbolTable);
		codeGenerator.processTacFile();
		
    }
	
	private static void checkMain() {
		if(RecursiveDescentParser.symbolTable.lookUp("main") == null) {
			System.out.println("ERROR - Program must contain function main!");
			System.exit(0);
		}
	}

}
