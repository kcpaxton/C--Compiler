
/***********************************************************
** Name: Kyle Paxton 
** Course: CSC 446
** Assignment: Assignment 1
** Date: 01/31/2018
************************************************************/

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
		
			Globals.token = LexicalAnalyzer.Symbol.unknownt;

			resWord.put("if", LexicalAnalyzer.Symbol.ift);
			resWord.put("else", LexicalAnalyzer.Symbol.elset);
			resWord.put("while", LexicalAnalyzer.Symbol.whilet);
			resWord.put("int", LexicalAnalyzer.Symbol.intt);
			resWord.put("float", LexicalAnalyzer.Symbol.floatt);
			resWord.put("char", LexicalAnalyzer.Symbol.chart);
			resWord.put("break", LexicalAnalyzer.Symbol.breakt);
			resWord.put("continue", LexicalAnalyzer.Symbol.continuet);
			resWord.put("void", LexicalAnalyzer.Symbol.voidt);
			
			characterSymbols.put("{", LexicalAnalyzer.Symbol.lbrackett);
			characterSymbols.put("}", LexicalAnalyzer.Symbol.rbrackett);
			characterSymbols.put("[", LexicalAnalyzer.Symbol.lsqrbrackett);
			characterSymbols.put("]", LexicalAnalyzer.Symbol.rsqrbrackett);
			characterSymbols.put("(", LexicalAnalyzer.Symbol.lparent);
			characterSymbols.put(")", LexicalAnalyzer.Symbol.rparent);
			characterSymbols.put("\"",LexicalAnalyzer.Symbol.quotationt );
			characterSymbols.put(",", LexicalAnalyzer.Symbol.commat);
			characterSymbols.put(";", LexicalAnalyzer.Symbol.semit);
			characterSymbols.put(".", LexicalAnalyzer.Symbol.periodt);
			characterSymbols.put("_", LexicalAnalyzer.Symbol.underscoret);
			characterSymbols.put( "==", LexicalAnalyzer.Symbol.relopt);
			characterSymbols.put("!=", LexicalAnalyzer.Symbol.relopt);
			characterSymbols.put("<", LexicalAnalyzer.Symbol.relopt);
			characterSymbols.put("<=", LexicalAnalyzer.Symbol.relopt);
			characterSymbols.put(">", LexicalAnalyzer.Symbol.relopt);
			characterSymbols.put(">=", LexicalAnalyzer.Symbol.relopt);
			characterSymbols.put("+", LexicalAnalyzer.Symbol.addopt);
			characterSymbols.put("-", LexicalAnalyzer.Symbol.addopt);
			characterSymbols.put("||", LexicalAnalyzer.Symbol.addopt);
			characterSymbols.put("&&", LexicalAnalyzer.Symbol.mulopt);
			characterSymbols.put("/", LexicalAnalyzer.Symbol.mulopt);
			characterSymbols.put("%", LexicalAnalyzer.Symbol.mulopt);
			characterSymbols.put("*", LexicalAnalyzer.Symbol.mulopt);
			characterSymbols.put("=", LexicalAnalyzer.Symbol.assignopt);
	        
			try {
				fileContents = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        LexicalAnalyzer.DisplayHeader();
        while(Globals.token != LexicalAnalyzer.Symbol.eoft)
        {
        	LexicalAnalyzer.GetNextToken();
        	LexicalAnalyzer.DisplayToken();
        	
        	LexicalAnalyzer.ClearLexeme();
        	
        }
        
    }
	

}
