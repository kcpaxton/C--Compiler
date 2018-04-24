
public class ConstantEntry extends BaseTableEntry{
	
	public ConstantEntry(String lexeme, LexicalAnalyzer.Symbol token, int depth) {
		super(lexeme, token, depth);
		// TODO Auto-generated constructor stub
	}
	//Union{
	public Integer value;
	public Float valueReal;
	//}

	public Integer getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public Float getValueReal() {
		return valueReal;
	}
	public void setValueReal(float valueReal) {
		this.valueReal = valueReal;
	}
}
