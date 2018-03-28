
public class FunctionEntry extends BaseTableEntry{
	public FunctionEntry(String lexeme, LexicalAnalyzer.Symbol token, int depth) {
		super(lexeme, token, depth);
		// TODO Auto-generated constructor stub
	}
	public int localSize;
	public int parameterCount;
	VariableEntry returnType;
	
	public int getLocalSize() {
		return localSize;
	}
	public void setLocalSize(int localSize) {
		this.localSize = localSize;
	}
	public int getParameterCount() {
		return parameterCount;
	}
	public void setParameterCount(int parameterCount) {
		this.parameterCount = parameterCount;
	}
	
	//ParameterEntry ParamList;
}
