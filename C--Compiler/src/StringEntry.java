
public class StringEntry extends BaseTableEntry{

	public StringEntry(String lexeme, LexicalAnalyzer.Symbol token, int depth) {
		super(lexeme, token, depth);
		// TODO Auto-generated constructor stub
	}
	public String name;
	
	public String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
