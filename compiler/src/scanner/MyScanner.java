package scanner;

import program_internal_form.ProgramInternalForm;
import program_internal_form.impl.ProgramInternalFormImpl;
import symbol_table.SymbolTable;
import symbol_table.impl.SymbolTableImpl;
import utils.Position;

import java.io.*;
import java.util.*;

public class MyScanner {
    private final Set<String> reservedWords;
    private final Set<String> operators;

    private final Set<String> specialOperators;
    private final Set<String> separators;

    private final Set<String> whiteSpaces;

    private final String identifierRegex;

    private final String integerConstantRegex;

    private final String characterConstantRegex;

    private final String stringConstantRegex;

    private SymbolTable identifiersSymbolTable;

    private SymbolTable constantsSymbolTable;

    private ProgramInternalForm programInternalForm;

    private Boolean isLexicallyCorrect;

    public MyScanner(String tokensFileName, String fileSeparator) {
        this.reservedWords = new HashSet<>();
        this.operators = new HashSet<>();
        this.specialOperators = new HashSet<>();
        this.separators = new HashSet<>();
        this.whiteSpaces = new HashSet<>(Set.of(" ", "\t"));
        this.identifierRegex = "^[a-zA-Z][a-zA-Z0-9]*$";
        this.integerConstantRegex = "^0|[+-]?[1-9][0-9]*$";
        this.characterConstantRegex = "^'[a-zA-Z0-9+*/%<=> ,:()_.?!-]?'$";
        this.stringConstantRegex = "^\"[a-zA-Z0-9+*/%<=> ,:()_.?!-]*\"$";
        this.identifiersSymbolTable = null;
        this.constantsSymbolTable = null;
        this.programInternalForm = null;
        this.isLexicallyCorrect = null;
        this.setup(tokensFileName, fileSeparator);
    }

    public void scan(String sourceCodeFileName, String symbolTablesFileName, String programInternalFormFileName) {
        this.identifiersSymbolTable = new SymbolTableImpl();
        this.constantsSymbolTable = new SymbolTableImpl();
        this.programInternalForm = new ProgramInternalFormImpl();
        this.isLexicallyCorrect = true;

        int index = 0;
        for (String line : this.readSourceCode(sourceCodeFileName)) {
            this.parseLine(line, index++);
        }
        this.printOutputFiles(symbolTablesFileName, programInternalFormFileName);
        if (this.isLexicallyCorrect) {
            System.out.println("The program is lexically correct");
        }

        this.identifiersSymbolTable = null;
        this.constantsSymbolTable = null;
        this.programInternalForm = null;
        this.isLexicallyCorrect = null;
    }

    public void scan(String sourceCodeFileName) {
        this.scan(sourceCodeFileName, "ST.out", "PIF.out");
    }

    private void setup(String tokensFileName, String fileSeparator) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(tokensFileName))) {
            this.parseTokensFile(bufferedReader.lines().toList(), fileSeparator);
        } catch (IOException ioException) {
            throw new RuntimeException(String.format("An error occurred while opening the %s file\n", tokensFileName), ioException);
        }
    }

    private void parseTokensFile(List<String> lines, String fileSeparator) {
        int index = 0;
        while (index < lines.size()) {
            String keyword = lines.get(index);
            if (fileSeparator.equals(keyword)) {
                break;
            }
            this.reservedWords.add(keyword);
            index++;
        }
        index++;
        while (index < lines.size()) {
            String operator = lines.get(index);
            if (fileSeparator.equals(operator)) {
                break;
            }
            if (operator.length() == 1) {
                this.operators.add(operator);
            } else {
                this.specialOperators.add(operator);
            }
            index++;
        }
        index++;
        while (index < lines.size()) {
            this.separators.add(lines.get(index));
            index++;
        }
    }

    private boolean isReservedWord(String token) {
        return this.reservedWords.contains(token);
    }

    private boolean isOperator(String token) {
        return this.operators.contains(token) || this.specialOperators.contains(token);
    }

    private boolean isSeparator(String token) {
        return this.separators.contains(token);
    }

    private boolean isIdentifier(String token) {
        return token.matches(this.identifierRegex);
    }

    private boolean isIntegerConstant(String token) {
        return token.matches(this.integerConstantRegex);
    }

    private boolean isCharacterConstant(String token) {
        return token.matches(this.characterConstantRegex);
    }

    private boolean isStringConstant(String token) {
        return token.matches(this.stringConstantRegex);
    }

    private boolean isConstant(String token) {
        return this.isIntegerConstant(token) || this.isCharacterConstant(token) || this.isStringConstant(token);
    }

    private void classifyToken(String token, Integer index) {
        if (" \t".contains(token)) {
            return;
        }
        if (this.isReservedWord(token)) {
            this.programInternalForm.insert(token, new Position(-1, -1));
        } else if (this.isOperator(token)) {
            this.programInternalForm.insert(token, new Position(-1, -1));
        } else if (this.isSeparator(token)) {
            this.programInternalForm.insert(token, new Position(-1, -1));
        } else if (this.isIdentifier(token)) {
            this.programInternalForm.insert("identifier", this.identifiersSymbolTable.insert(token));
        } else if (this.isConstant(token)) {
            if (this.isIntegerConstant(token) && token.contains("+")) {
                token = token.substring(1);
            }
            this.programInternalForm.insert("constant", this.constantsSymbolTable.insert(token));
        } else {
            System.out.printf("LEXICAL ERROR line %d, token \"%s\"\n", index, token);
            this.isLexicallyCorrect = false;
        }
    }

    private List<String> tokenizeLine(String line) {
        List<String> tokens = new LinkedList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(line, String.join("", this.whiteSpaces) + String.join("", this.separators) + String.join("", this.operators), true);
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (!this.whiteSpaces.contains(token)) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private void parseSimpleLine(String line, Integer index) {
        Iterator<String> tokens = this.tokenizeLine(line).iterator();
        if (!tokens.hasNext()) {
            return;
        }
        String prevToken, token, nextToken;
        prevToken = "";
        token = tokens.next();
        while (tokens.hasNext()) {
            nextToken = tokens.next();
            if (this.specialOperators.contains(token + nextToken) || (this.isIntegerConstant(token + nextToken) && (this.isReservedWord(prevToken) || this.isOperator(prevToken)))) {
                token += nextToken;
                if (tokens.hasNext()) {
                    nextToken = tokens.next();
                } else {
                    nextToken = null;
                }
            }
            this.classifyToken(token, index);
            prevToken = token;
            token = nextToken;
        }
        if (token != null) {
            this.classifyToken(token, index);
        }

    }

    private void parseLineWithConstants(String line, String delimiter, Integer index) {
        StringTokenizer stringTokenizer = new StringTokenizer(line, delimiter, true);
        StringBuilder partialLine = new StringBuilder();
        StringBuilder stringConstant = new StringBuilder();
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (delimiter.equals(token)) {
                if (stringConstant.isEmpty()) {
                    this.parseSimpleLine(partialLine.toString(), index);
                    partialLine = new StringBuilder();
                    stringConstant = new StringBuilder(token);
                } else {
                    stringConstant.append(token);
                    this.classifyToken(stringConstant.toString(), index);
                    stringConstant = new StringBuilder();
                }
            } else {
                if (stringConstant.isEmpty()) {
                    partialLine.append(token);
                } else {
                    stringConstant.append(token);
                }
            }
        }
        if (stringConstant.isEmpty()) {
            this.parseLine(partialLine.toString(), index);
        } else {
            this.classifyToken(stringConstant.toString(), index);
        }
    }

    private void parseLineWithCharacterConstants(String line, Integer index) {
        this.parseLineWithConstants(line, "'", index);
    }

    private void parseLineWithStringConstants(String line, Integer index) {
        this.parseLineWithConstants(line, "\"", index);
    }

    private void parseLine(String line, Integer index) {
        if (line.contains("'")) {
            this.parseLineWithCharacterConstants(line, index);
        } else if (line.contains("\"")) {
            this.parseLineWithStringConstants(line, index);
        } else {
            this.parseSimpleLine(line, index);
        }
    }

    private List<String> readSourceCode(String sourceCodeFileName) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(sourceCodeFileName))) {
            return bufferedReader.lines().skip(1).toList();
        } catch (IOException ioException) {
            throw new RuntimeException(String.format("An error occurred while opening the %s file\n", sourceCodeFileName), ioException);
        }
    }

    private void printSymbolTables(String symbolTablesFileName) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(symbolTablesFileName))) {
            bufferedWriter.write(SymbolTableImpl.implementationDetails());
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write("Identifiers Symbol Table");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write(this.identifiersSymbolTable.toString());
            bufferedWriter.newLine();
            bufferedWriter.write("Constant Symbol Table");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write(this.constantsSymbolTable.toString());
        } catch (IOException ioException) {
            throw new RuntimeException(String.format("An error occurred while opening the output file %s for the Symbol Tables\n", symbolTablesFileName), ioException);
        }
    }

    private void printProgramInternalForm(String programInternalFormFileName) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(programInternalFormFileName))) {
            bufferedWriter.write(ProgramInternalFormImpl.implementationDetails());
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write("Program Internal Form");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write(this.programInternalForm.toString());
        } catch (IOException ioException) {
            throw new RuntimeException(String.format("An error occurred while opening the output file %s for the Program Internal Forms\n", programInternalFormFileName), ioException);
        }
    }

    private void printOutputFiles(String symbolTablesFileName, String programInternalFormFileName) {
        this.printSymbolTables(symbolTablesFileName);
        this.printProgramInternalForm(programInternalFormFileName);
    }
}
