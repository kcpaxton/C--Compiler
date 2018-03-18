/* ********************************************************
 * Name: Kyle Paxton 
 * Course: CSC 446
 * Assignment: Assignment 3
 * Date: 02/21/2018
 **********************************************************/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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




public class Main {
	static String fileContents = "";
	static Scanner scanner;
	
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
		characterSymbols.put("=", LexicalAnalyzer.Symbol.assignoptToken);
        
//		try {
//			fileContents = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		
//		LexicalAnalyzer.GetNextToken();
//		
//		RecursiveDescentParser.Prog();
//		
//		if(Globals.token == LexicalAnalyzer.Symbol.eofToken)
//		{
//			System.out.println("Successful Compilation!");
//		}
//		else {
//			System.out.println("ERROR - Unused Tokens!");
//		}
//        
		SymbolTable symbolTable = new SymbolTable();
		symbolTable.insert("hello", LexicalAnalyzer.Symbol.addoptToken, 1);
		symbolTable.insert("cya", LexicalAnalyzer.Symbol.identifierToken, 1);
		symbolTable.insert("hey",LexicalAnalyzer.Symbol.addoptToken, 3);
		symbolTable.insert("hello", LexicalAnalyzer.Symbol.addoptToken, 2);
		symbolTable.insert("yo", LexicalAnalyzer.Symbol.muloptToken, 2);
		symbolTable.insert("hey", LexicalAnalyzer.Symbol.intToken, 3);
		symbolTable.insert("hi", LexicalAnalyzer.Symbol.identifierToken, 2);
		
		//SymbolTable.deleteDepth(1);
		//SymbolTable.deleteDepth(3);
		//SymbolTable.deleteDepth(2);
		SymbolTable.writeTable(3);
		SymbolTable.writeTable(2);
		SymbolTable.writeTable(1);
    }
	

}
