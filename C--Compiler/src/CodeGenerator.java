import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CodeGenerator {
	
	static PrintWriter codeWriter = null;
	static String fileContents = "";
	SymbolTable symbolTable;
	static String endOfLine = System.getProperty("line.separator");
	BaseTableEntry functionEntry;
	public CodeGenerator(String fileName, SymbolTable symbolTable) {
		
		this.symbolTable = symbolTable;
		
		try {
			fileContents = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fileName =fileName.substring(0, fileName.indexOf("."));
			codeWriter = new PrintWriter(fileName+".asm",  "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void processTacFile() {
		printAsmHeader();
		
		String[] lines = fileContents.split(endOfLine);
		while(lines.length != 0) {
			
			if(lines[0].startsWith("Proc")) {
				printProcedure(lines[0]);
			}
			else if(lines[0].contains("call")){
				outputAsm(lines[0]);
			}
			else if(lines[0].contains("wrs") ) {
				printWriteString(lines[0]);
			}
			else if(lines[0].contains("wri") ) {
				printWriteInt(lines[0]);
			}
			else if(lines[0].contains("wrln") ) {
				printEndLine(lines[0]);
			}
			else if(lines[0].contains("rdi") ) {
				printReadInt(lines[0]);
			}
			else if(lines[0].contains("+")) {
				printAddStatement(lines[0]);
			}
			else if(lines[0].contains("- ")) {
				printSubtractionStatement(lines[0]);
			}
			else if(lines[0].contains("/")) {
				printDivisionStatement(lines[0]);
			}
			else if(lines[0].contains("*")) {
				printMultiplicationStatement(lines[0]);
			}
			else if(lines[0].contains("%")) {
				printModuloStatement(lines[0]);
			}
			else if(lines[0].contains("=")) {
				printCopystatement(lines[0]);			
			}
			else if(lines[0].contains("Endp")) {
				printEndProcedure(lines[0]);
			}
			
			lines = Arrays.copyOfRange(lines, 1, lines.length);
		}
		printMainFunction();
		codeWriter.close();
	}
	
	


	private void printReadInt(String tacLine) {
		String splitTacLine[] = tacLine.split(" ");
		outputAsm("call readint");
		outputAsm("mov " +  updateBP(splitTacLine[1]) + ", bx");
	}

	private void printEndLine(String tacLine) {
		outputAsm("call writeln");
		
	}

	private void printMultiplicationStatement(String tacLine) {
		String splitTacLine[] = tacLine.split(" ");
		outputAsm("mov ax, " + updateBP(splitTacLine[2]));
		outputAsm("mov cx, " + updateBP(splitTacLine[4]));
		outputAsm("mul cx");
		outputAsm("mov " + updateBP(splitTacLine[0]) + ", ax");
		
	}

	private void printSubtractionStatement(String tacLine) {
		String splitTacLine[] = tacLine.split(" ");
		outputAsm("mov ax, " + updateBP(splitTacLine[2]));
		outputAsm("mov bx, " + updateBP(splitTacLine[4]));
		outputAsm("sub ax, bx");
		outputAsm("mov " + updateBP(splitTacLine[0]) + ", ax");
		
	}


	private void printModuloStatement(String tacLine) {
		String splitTacLine[] = tacLine.split(" ");
		outputAsm("mov dx, 0");
		outputAsm("mov ax, " + updateBP(splitTacLine[2]));
		outputAsm("mov cx, " + updateBP(splitTacLine[4]));
		outputAsm("div cx");
		outputAsm("mov " + updateBP(splitTacLine[0]) + ", dx");
	}

	private void printDivisionStatement(String tacLine) {
		String splitTacLine[] = tacLine.split(" ");
		outputAsm("mov dx, 0");
		outputAsm("mov ax, " + updateBP(splitTacLine[2]));
		outputAsm("mov cx, " + updateBP(splitTacLine[4]));
		outputAsm("div cx");
		outputAsm("mov " + updateBP(splitTacLine[0]) + ", ax");
		
	}

	private void printWriteInt(String tacLine) {
		String splitTacLine[] = tacLine.split(" ");
		outputAsm("mov ax, " + updateBP(splitTacLine[1]));
		outputAsm("call writeint");
	}

	private void printWriteString(String tacLine) {
		String splitTacLine[] = tacLine.split(" ",2);
		
		outputAsm("mov dx, offset " + splitTacLine[1]);
		outputAsm("call writestr");
		
		
	}

	private void printAddStatement(String tacLine) {
		String splitTacLine[] = tacLine.split(" ");
		outputAsm("mov ax, " + updateBP(splitTacLine[2]));
		outputAsm("mov bx, " + updateBP(splitTacLine[4]));
		outputAsm("add ax, bx");
		outputAsm("mov " + updateBP(splitTacLine[0]) + ", ax");
	}

	private void printProcedure(String tacLine) {
		
		String splitTacLine[] = tacLine.split(" ");
		functionEntry = symbolTable.lookUp(splitTacLine[1]);
		outputAsm("");
		outputAsm(updateBP(splitTacLine[1]) +" PROC");
		outputAsm("push bp");
		outputAsm("mov bp, sp");
		outputAsm("sub sp, "+ ((FunctionEntry)functionEntry).getLocalSize());
		outputAsm("");
	}

	private void printEndProcedure(String tacLine) {
		String splitTacLine[] = tacLine.split(" ");
		functionEntry = symbolTable.lookUp(splitTacLine[1]);
		outputAsm("");
		outputAsm("add sp, " + ((FunctionEntry)functionEntry).getLocalSize());
		outputAsm("pop bp");
		outputAsm("ret 0");
		outputAsm(updateBP(splitTacLine[1]) + " ENDP");
		outputAsm("");
	}
	//[BP-X]
	private void printCopystatement(String tacLine) {
		
		//a = b
		String splitTacLine[] = tacLine.split("=");
		String printLine = "mov ax, ";
		printLine += updateBP(splitTacLine[1]);// mov ax, b
		outputAsm(printLine);
		printLine = "mov ";
		printLine += updateBP(splitTacLine[0]);
		printLine += ", ax";
		outputAsm(printLine);		
	}
	
	private void printAsmHeader() {
		outputAsm(".model small");
		outputAsm(".stack 100h");
		outputAsm(".data");
		printIntVariables();
		printStringLiterals();
		outputAsm(".code");
		outputAsm("include io.asm");
	}

	private void printMainFunction() {
		outputAsm("");
		outputAsm("_startproc PROC");
		outputAsm("mov ax, @data");
		outputAsm("mov ds, ax");
		outputAsm("call main");
		outputAsm("mov ax, 4c00h");
		outputAsm("int 21h");
		outputAsm("_startproc ENDP");
		outputAsm("END _startproc");
		outputAsm("");
		
	}
	
	
	private void printIntVariables() {
		List<VariableEntry> list = symbolTable.getIntegers();
		if(!list.isEmpty()) {
			String line = null;
			for (VariableEntry variableEntry : list) {
				line = variableEntry.getLexeme();
				line += " DW ";
				line += "?";
				outputAsm(line);
			}
			
		}
	}

	private void printStringLiterals() {
		List<StringEntry> list = symbolTable.getStringLiterals();
		if(!list.isEmpty()) {
			String line = null;
			int i = 0;
			for (StringEntry stringEntry : list) {
				line = stringEntry.getName();
				line += " DB ";
				line += stringEntry.getValue();
				line += ", \"$\"";
				i++;
				outputAsm(line);
			}
		}
	}
	
	private String updateBP(String BP) {
		BP = BP.trim();
		
		if(BP.startsWith("_bp")) {	
			if(BP.charAt(3) != '-') {
				BP = BP.substring(0, 3) + "+" + BP.substring(3, BP.length());
			}
			BP = BP.substring(1, BP.length());
			return "[" + BP + "]";
		}
		else {
			return BP;
		}
	}
	
	private void outputAsm(String line) {
		codeWriter.println(line);
		System.out.println(line);
	}
}
