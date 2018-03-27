
public class VariableEntry extends BaseTableEntry{
	
	public VariableEntry(String lexeme, LexicalAnalyzer.Symbol token, int depth) {
		super(lexeme, token, depth);
		// TODO Auto-generated constructor stub
	}
	public VariableEntry variableType;
	public int offset;
	public int size;
	
	public VariableEntry getVariableType() {
		return variableType;
	}
	public void setVariableType(VariableEntry variableType) {
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
