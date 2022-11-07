package scanner;

import program_internal_form.ProgramInternalForm;
import program_internal_form.impl.ProgramInternalFormImpl;
import symbol_table.SymbolTable;
import symbol_table.impl.SymbolTableImpl;
import utils.Position;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MyScanner {
    private final Set<String> reservedWords;
    private final Set<String> operators;

    private final Set<String> specialOperators;
    private final Set<String> separators;

    private final String identifierRegex;

    private final String integerConstantRegex;

    private final String characterConstantRegex;

    private final String stringConstantRegex;

    private Boolean isLexicallyCorrect;

    public MyScanner(String tokensFileName, String fileSeparator) {
        this.reservedWords = new HashSet<>();
        this.operators = new HashSet<>();
        this.specialOperators = new HashSet<>();
        this.separators = new HashSet<>();
        this.identifierRegex = "^[a-zA-Z][a-zA-Z0-9]*$";
        this.integerConstantRegex = "^0|[+-]?[1-9][0-9]*$";
        this.characterConstantRegex = "^'[a-zA-Z0-9+*/%<=> ,:()_.?!-]?'$";
        this.stringConstantRegex = "^\"[a-zA-Z0-9+*/%<=> ,:()_.?!-]*\"$";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(tokensFileName))) {
            List<String> lines = bufferedReader.lines().toList();
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
        } catch (Exception e) {
            System.err.printf("An error occurred while opening the %s file\n" + e + "\n", tokensFileName);
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

    private void classifyToken(String token, Integer index, SymbolTable sti, SymbolTable stc, ProgramInternalForm pif) {
        if (" ".equals(token) || "\t".equals(token)) {
            return;
        }
        if (this.isReservedWord(token)) {
            pif.insert(token, new Position(-1, -1));
        } else if (this.isOperator(token)) {
            pif.insert(token, new Position(-1, -1));
        } else if (this.isSeparator(token)) {
            pif.insert(token, new Position(-1, -1));
        } else if (this.isIdentifier(token)) {
            pif.insert("identifier", sti.insert(token));
        } else if (this.isConstant(token)) {
            pif.insert("constant", stc.insert(token));
        } else {
            System.out.printf("LEXICAL ERROR line %d, token \"%s\"\n", index, token);
            this.isLexicallyCorrect = false;
        }
    }

    private void parseStringConstant(String token, Integer index, SymbolTable stc, ProgramInternalForm pif) {
        if (this.isStringConstant(token)) {
            pif.insert("constant", stc.insert(token));
        } else {
            System.out.printf("LEXICAL ERROR line %d, token \"%s\"\n", index, token);
            this.isLexicallyCorrect = false;
        }
    }

    private void parseLineWithoutStringConstants(String line, Integer index, SymbolTable sti, SymbolTable stc, ProgramInternalForm pif) {
        StringTokenizer stringTokenizer = new StringTokenizer(line, " \t" + String.join("", this.separators) + String.join("", this.operators), true);
        Set<String> specialOperatorsFirst = this.specialOperators.stream().map(el -> el.substring(0, 1)).collect(Collectors.toSet());
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (specialOperatorsFirst.contains(token)) {
                if (stringTokenizer.hasMoreTokens()) {
                    String nextToken = stringTokenizer.nextToken();
                    if (this.specialOperators.contains(token + nextToken)) {
                        token += nextToken;
                    } else {
                        this.classifyToken(token, index, sti, stc, pif);
                        this.classifyToken(nextToken, index, sti, stc, pif);
                        continue;
                    }
                }
            }
            this.classifyToken(token, index, sti, stc, pif);
        }
    }

    public void parseLineWithStringConstants(String line, Integer index, SymbolTable sti, SymbolTable stc, ProgramInternalForm pif) {
        StringBuilder partialLine = new StringBuilder();
        StringBuilder stringConstant = new StringBuilder();
        StringTokenizer stringTokenizer = new StringTokenizer(line, "\"", true);
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if ("\"".equals(token)) {
                if (stringConstant.isEmpty()) {
                    this.parseLineWithoutStringConstants(partialLine.toString(), index, sti, stc, pif);
                    partialLine = new StringBuilder();
                    stringConstant.append(token);
                } else {
                    stringConstant.append(token);
                    this.parseStringConstant(stringConstant.toString(), index, stc, pif);
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
        if (!partialLine.isEmpty()) {
            this.parseLineWithoutStringConstants(partialLine.toString(), index, sti, stc, pif);
        } else {
            this.parseStringConstant(stringConstant.toString(), index, stc, pif);
        }
    }


    public void scan(String sourceCodeFileName, String symbolTableFileName, String programInternalFormFileName) {
        ProgramInternalForm pif;
        SymbolTable sti, stc;
        this.isLexicallyCorrect = true;
        pif = new ProgramInternalFormImpl();
        sti = new SymbolTableImpl();
        stc = new SymbolTableImpl();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(sourceCodeFileName))) {
            List<String> lines = bufferedReader.lines().skip(1).toList();
            int index = 0;
            for (String line : lines) {
                if (line.contains("\"")) {
                    this.parseLineWithStringConstants(line, index, sti, stc, pif);
                } else {
                    this.parseLineWithoutStringConstants(line, index, sti, stc, pif);
                }
                index++;
            }
        } catch (Exception e) {
            System.err.printf("An error occurred while opening the %s file\n" + e + "\n", sourceCodeFileName);
        }
        try (BufferedWriter stBufferedWriter = new BufferedWriter(new FileWriter(symbolTableFileName));
             BufferedWriter pifBufferedWriter = new BufferedWriter(new FileWriter(programInternalFormFileName))) {
            stBufferedWriter.write("Symbol Table implemented on a hash table with collision resolution through separate chaining\n");
            stBufferedWriter.write("\nIdentifiers Symbol Table\n\n");
            stBufferedWriter.write(sti.toString());
            stBufferedWriter.write("\nConstants Symbol Table\n\n");
            stBufferedWriter.write(stc.toString());
            pifBufferedWriter.write("Program Internal Form implemented on a singly linked list");
            pifBufferedWriter.write("\nProgram Internal Form\n\n");
            pifBufferedWriter.write(pif.toString());
            if (this.isLexicallyCorrect) {
                System.out.println("The program is lexically correct");
            }
        } catch (Exception e) {
            System.err.printf("An error occurred while opening the output files %s and %s\n" + e + "\n", symbolTableFileName, programInternalFormFileName);
        }
    }

    public void scan(String sourceCodeFileName) {
        this.scan(sourceCodeFileName, "ST.out", "PIF.out");
    }
}
