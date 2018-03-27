
public class ConstantEntry extends BaseTableEntry{
	
	public ConstantEntry(String lexeme, LexicalAnalyzer.Symbol token, int depth) {
		super(lexeme, token, depth);
		// TODO Auto-generated constructor stub
	}
	//Union{
	public int value;
	public float valueReal;
	//}

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public float getValueReal() {
		return valueReal;
	}
	public void setValueReal(float valueReal) {
		this.valueReal = valueReal;
	}
}
