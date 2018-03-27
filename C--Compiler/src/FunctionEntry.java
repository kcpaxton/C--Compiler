
public class FunctionEntry extends BaseTableEntry{
	public FunctionEntry(String lexeme, LexicalAnalyzer.Symbol token, int depth) {
		super(lexeme, token, depth);
		// TODO Auto-generated constructor stub
	}
	public int localSize;
	public int parameterCount;
	VariableEntry returnType;
	//ParameterEntry ParamList;
}
