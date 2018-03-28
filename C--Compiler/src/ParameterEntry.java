
public class ParameterEntry extends BaseTableEntry {
	
	public ParameterEntry(String lexeme, LexicalAnalyzer.Symbol token, int depth) {
		super(lexeme, token, depth);
		// TODO Auto-generated constructor stub
	}

	public VariableEntry parameterType;

	public VariableEntry getParameterType() {
		return parameterType;
	}

	public void setParameterType(VariableEntry parameterType) {
		this.parameterType = parameterType;
	}
	
}
