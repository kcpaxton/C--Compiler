import java.lang.invoke.ConstantCallSite;
import java.sql.Struct;

public class SymbolTable {

	public static final int TableSize = 211;
	
	enum VariableType {charType, intType, floatType};
	enum EntryType { variableType, constType, functionType}; 
	
	
	 
	
	
	final class ParamNode{
		VariableType typeOfParameter;
		
	}
}
