
public class BaseTableEntry {
	
	public BaseTableEntry(String lexeme,LexicalAnalyzer.Symbol token, int depth) {
		super();
		this.lexeme = lexeme;
		this.token = token;
		this.depth = depth;
	}
	public LexicalAnalyzer.Symbol token;
    public String lexeme;
    public int depth;
    
    public LexicalAnalyzer.Symbol getToken() {
		return token;
	}
	public void setToken(LexicalAnalyzer.Symbol token) {
		this.token = token;
	}
	public String getLexeme() {
		return lexeme;
	}
	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
}
