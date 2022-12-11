package grammar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    private final Set<String> nonterminalSymbols;

    private final Set<String> terminalSymbols;

    private String startSymbol;

    private final Map<List<String>, List<List<String>>> productions;

    public Grammar(String fileName) {
        this.nonterminalSymbols = new HashSet<>();
        this.terminalSymbols = new HashSet<>();
        this.productions = new HashMap<>();
        this.readFromFile(fileName);
    }

    public Set<String> getNonterminalSymbols() {
        return this.nonterminalSymbols;
    }

    public Set<String> getTerminalSymbols() {
        return this.terminalSymbols;
    }

    public String getStartSymbol() {
        return this.startSymbol;
    }

    public Map<List<String>, List<List<String>>> getProductions() {
        return this.productions;
    }

    public boolean isContextFree() {
        return this.productions.keySet().stream().allMatch(rhs -> rhs.size() == 1);
    }

    public Map<List<String>, List<List<String>>> getProductionsForNonterminal(String nonterminal) {
        return this.productions.entrySet().stream().filter(production -> production.getKey().contains(nonterminal)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void readFromFile(String fileName) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            List<String> lines;
            StringTokenizer stringTokenizer;
            int index;

            lines = bufferedReader.lines().toList();
            if (lines.size() < 4) {
                throw new RuntimeException(String.format("The input file %s does not have the required format", fileName));
            }

            stringTokenizer = new StringTokenizer(lines.get(0), "# ");
            while (stringTokenizer.hasMoreTokens()) {
                this.nonterminalSymbols.add(stringTokenizer.nextToken());
            }

            stringTokenizer = new StringTokenizer(lines.get(1), "# ");
            while (stringTokenizer.hasMoreTokens()) {
                this.terminalSymbols.add(stringTokenizer.nextToken());
            }

            if (!this.nonterminalSymbols.contains(lines.get(2))) {
                throw new RuntimeException(String.format("The input file %s does not have the required format", fileName));
            }
            this.startSymbol = lines.get(2);

            index = 3;
            while (index < lines.size()) {
                stringTokenizer = new StringTokenizer(lines.get(index), "@");
                if (stringTokenizer.countTokens() != 2) {
                    throw new RuntimeException(String.format("The input file %s does not have the required format", fileName));
                }
                this.productions.put(this.parseRHS(lines.get(index), stringTokenizer.nextToken()), this.parseLHS(lines.get(index), stringTokenizer.nextToken()));
                index++;
            }
        } catch (IOException ioException) {
            throw new RuntimeException(String.format("An error appeared while opening the file %s", fileName), ioException);
        }
    }

    private List<List<String>> parseLHS(String production, String lhs) {
        List<List<String>> lhsList = new LinkedList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(lhs, "|");
        while (stringTokenizer.hasMoreTokens()) {
            lhsList.add(this.parseLHSProduction(production, stringTokenizer.nextToken()));
        }
        return lhsList;
    }

    private List<String> parseLHSProduction(String production, String lhs) {
        List<String> lhsList = new LinkedList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(lhs);
        while (stringTokenizer.hasMoreTokens()) {
            String currentToken = stringTokenizer.nextToken();
            if ("&".equals(currentToken)) {
                lhsList.add("ε");
                continue;
            }
            if (!this.nonterminalSymbols.contains(currentToken) && !this.terminalSymbols.contains(currentToken)) {
                throw new RuntimeException(String.format("The left hand side of the production %s is invalid", production));
            }
            lhsList.add(currentToken);
        }
        return lhsList;
    }

    private List<String> parseRHS(String production, String rhs) {
        boolean hasNonterminal = false;
        List<String> rhsList = new LinkedList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(rhs);
        while (stringTokenizer.hasMoreTokens()) {
            String currentToken = stringTokenizer.nextToken();
            if (!this.nonterminalSymbols.contains(currentToken) && !this.terminalSymbols.contains(currentToken)) {
                throw new RuntimeException(String.format("The right hand side of the production %s is invalid", production));
            }
            if (this.nonterminalSymbols.contains(currentToken) && !hasNonterminal) {
                hasNonterminal = true;
            }
            rhsList.add(currentToken);
        }
        if (!hasNonterminal) {
            throw new RuntimeException(String.format("The right hand side of the production %s does not contain any nonterminal", production));
        }
        return rhsList;
    }
}
