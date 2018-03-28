
public class VariableEntry extends BaseTableEntry{
	
	public VariableEntry(String lexeme, LexicalAnalyzer.Symbol token, int depth) {
		super(lexeme, token, depth);
		// TODO Auto-generated constructor stub
	}
	public LexicalAnalyzer.Symbol variableType;
	public int offset;
	public int size;
	
	public LexicalAnalyzer.Symbol getVariableType() {
		return variableType;
	}
	public void setVariableType(LexicalAnalyzer.Symbol variableType) {
		this.variableType = variableType;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
}
