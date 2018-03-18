import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.x500.X500Principal;

public class SymbolTable {
	
	static int tableSize = 211;
	static LinkedList<BaseTableEntry>[] myHashTable;
	
	public SymbolTable() {
		myHashTable=new LinkedList[tableSize];		
	}
	
	public void insert(String lexeme, LexicalAnalyzer.Symbol token, int depth) {
		BaseTableEntry tempNode = new BaseTableEntry(lexeme, token, depth);
		
		int hashedLocation = hash(lexeme);
		if (myHashTable[hashedLocation] == null) {
			myHashTable[hashedLocation] = new LinkedList<BaseTableEntry>();
		}
		
		myHashTable[hashedLocation].addFirst(tempNode);
	}
	 
	public BaseTableEntry lookUp(String lexeme) {
		int hashedLocation = hash(lexeme);
		return myHashTable[hashedLocation].getFirst();
	}
	
	public static void deleteDepth(int depth) {
		//count through foreach to modify actual value of table
		int count = 0;
		for (LinkedList<BaseTableEntry> i : myHashTable) {
			if(i != null && i.getFirst().getDepth() == depth) {		
				myHashTable[count] = null;
			}
			count++;
		}
	}
	
	
	public static void writeTable(int depth) {
		 System.out.println("**************************************************************************");
		 System.out.printf("%-30.30s  %-30.30s  %-30.30s%n" , "Depth", "Lexeme", "Token");
		 System.out.println("**************************************************************************");
		
		 for(LinkedList<BaseTableEntry> i : myHashTable) {
			 if(i != null) {
				 if(i.getFirst().getDepth() == depth) {
					 System.out.printf("%-30.30s  %-30.30s  %-30.30s%n" , i.getFirst().getDepth(), i.getFirst().getLexeme(), i.getFirst().getToken());
				 }
			 }
		 }
		 System.out.println("**************************************************************************");
		 System.out.println();
		 System.out.println();
	}
	
	
	public static int hash(String lexeme) {
		return (lexeme.hashCode() % tableSize);
	}
}

	 
 