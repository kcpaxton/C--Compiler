import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import javax.security.auth.x500.X500Principal;

public class SymbolTable {
	
	static int tableSize = 211;
	static LinkedList<BaseTableEntry>[] myHashTable;
	
	@SuppressWarnings("unchecked")
	public SymbolTable() {
		myHashTable=new LinkedList[tableSize];		
	}
	
	public void insert(BaseTableEntry newEntry) {
		BaseTableEntry tempNode = lookUp(newEntry.lexeme);
		if(tempNode == null) {	
			int hashedLocation = hash(newEntry.lexeme);
			myHashTable[hashedLocation] = new LinkedList<BaseTableEntry>();
			
			myHashTable[hashedLocation].addFirst(newEntry);
		}
		else if(tempNode.getDepth() == newEntry.depth){
			System.out.println("Error: Cannot insert lexeme: " + newEntry.lexeme+ " at depth: "+ newEntry.depth);
		}
		else {		
			int hashedLocation = hash(newEntry.lexeme);			
			myHashTable[hashedLocation].addFirst(newEntry);
		}
		
	}
	 
	public BaseTableEntry lookUp(String lexeme) {
		int hashedLocation = hash(lexeme);
		if(myHashTable[hashedLocation] == null) {
			return null;
		}
		else {
			return myHashTable[hashedLocation].getFirst();
		}
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
	
	public void writeTable(int depth) {
		System.out.println("Printing at depth: " + depth);
		System.out.println("**************************************************************************");
		 System.out.printf("%-30.30s  %-30.30s  %-30.30s%n" ,"class" , "Lexeme", "Depth");
		 System.out.println("**************************************************************************"); 
		 
		 for(LinkedList<BaseTableEntry> i : myHashTable) {
			 if(i != null) {
				 for(BaseTableEntry j : i) {
					 if(j.getDepth() == depth) {
						 	String instanceClass = "";
						 	if(j instanceof VariableEntry) {
						 		instanceClass = "Variable";
						 	}
						 	else if(j instanceof ConstantEntry) {
						 		instanceClass = "Const";
						 	}
						 	else {
						 		instanceClass = "Function";
						 	}
							 System.out.printf("%-30.30s  %-30.30s  %-30.30s%n" , instanceClass, j.getLexeme(), j.getDepth());
					 }
				 }
			 }
		 }
		 System.out.println("**************************************************************************");
		 System.out.println();
		 System.out.println();
	}
	public void myWriteTable(int depth) {
		  
		System.out.println("**************************************************************************");
		 System.out.printf("%-15.15s  %-15.15s  %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s%n" ,"class" , "Depth", "Lexeme", "Token", "Offset", "Size", "Value", "ValueR", "parameterCount", "localSize");
		 System.out.println("**************************************************************************"); 
		 
		 for(LinkedList<BaseTableEntry> i : myHashTable) {
			 if(i != null) {
				 for(BaseTableEntry j : i) {
					 if(j.getDepth() == depth) {
						 String instanceClass = "";
						 if(j instanceof VariableEntry){
							 instanceClass = "Variable";
							 System.out.printf("%-15.15s  %-15.15s  %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s%n" ,instanceClass, j.getDepth(), j.getLexeme(), j.getToken(),
						     ((VariableEntry) j).getOffset(), ((VariableEntry) j).getSize(), " - ", " - ");
						 }
						 else if(j instanceof ConstantEntry){
							 instanceClass = "Const";
							 System.out.printf("%-15.15s  %-15.15s  %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s%n" ,instanceClass, j.getDepth(), j.getLexeme(), j.getToken(),
									 " - ", " - ", ((ConstantEntry) j).getValue(), ((ConstantEntry) j).getValueReal());
						 }
						 else if(j instanceof FunctionEntry){
							 instanceClass = "Function";
							 System.out.printf("%-15.15s  %-15.15s  %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s%n" , instanceClass, j.getDepth(), j.getLexeme(), j.getToken(),
									 " - ", " - ", " - ", " - ", ((FunctionEntry) j).getParameterCount(), ((FunctionEntry) j).getLocalSize());
						 }
						 else if(j instanceof ParameterEntry){
							 instanceClass = "Parameter";
							 System.out.printf("%-15.15s  %-15.15s  %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s %-15.15s%n" , instanceClass, j.getDepth(), j.getLexeme(), j.getToken(),
									 "-", " - ", " - ", " - ", "-", "-");
						 }
						 else {
							 System.out.printf("%-30.30s  %-30.30s  %-30.30s%n" , j.getDepth(), j.getLexeme(), j.getToken());
						 }
						 
					 }
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

	 
 