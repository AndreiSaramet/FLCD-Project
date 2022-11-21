package finite_automaton.impl;

import finite_automaton.FiniteAutomaton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FiniteAutomatonImpl implements FiniteAutomaton {
    private final Set<String> states;

    private final Set<String> alphabet;

    private String initialState;

    private final Set<String> finalStates;

    private final Map<Map.Entry<String, String>, List<String>> transitions;

    public FiniteAutomatonImpl(String faFileName) {
        this.states = new HashSet<>();
        this.alphabet = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.transitions = new HashMap<>();
        this.read(faFileName);
    }

    public Set<String> getStates() {
        return states;
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public String getInitialState() {
        return initialState;
    }

    public Set<String> getFinalStates() {
        return finalStates;
    }

    public Map<Map.Entry<String, String>, List<String>> getTransitions() {
        return transitions;
    }

    @Override
    public boolean accepts(String sequence) {
        if (!this.isDeterministic()) {
            System.out.println("The automaton is nondeterministic");
            return false;
        }
        StringBuilder currentSequence = new StringBuilder(sequence);
        String currentState = this.initialState;
        while (!currentSequence.isEmpty()) {
            String currentTerminal = currentSequence.substring(0, 1);
            List<String> results = this.transitions.get(new AbstractMap.SimpleImmutableEntry<>(currentState, currentTerminal));
            if (results == null) {
                break;
            }
            currentSequence.delete(0, 1);
            currentState = results.get(0);
        }
        return this.finalStates.contains(currentState) && currentSequence.isEmpty();
    }

    private void read(String faFileName) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(faFileName))) {
            List<String> lines = bufferedReader.lines().toList();
            int index;
            if (lines.size() < 5) {
                System.err.printf("The input file %s does not have the required format", faFileName);
                return;
            }
            StringTokenizer stringTokenizer;
            stringTokenizer = new StringTokenizer(lines.get(0), "|");
            while (stringTokenizer.hasMoreTokens()) {
                this.states.add(stringTokenizer.nextToken());
            }
            stringTokenizer = new StringTokenizer(lines.get(1), "|");
            while (stringTokenizer.hasMoreTokens()) {
                this.alphabet.add(stringTokenizer.nextToken());
            }
            this.initialState = lines.get(2);
            stringTokenizer = new StringTokenizer(lines.get(3), "|");
            while (stringTokenizer.hasMoreTokens()) {
                this.finalStates.add(stringTokenizer.nextToken());
            }
            index = 4;
            while (index < lines.size()) {
                stringTokenizer = new StringTokenizer(lines.get(index), "|");
                if (stringTokenizer.countTokens() != 3) {
                    System.err.printf("The input file %s does not have the required format", faFileName);
                    return;
                }
                String s1, x, s2;
                s1 = stringTokenizer.nextToken();
                x = stringTokenizer.nextToken();
                s2 = stringTokenizer.nextToken();
                if (!this.states.contains(s1) || !this.alphabet.contains(x) || !this.states.contains(s2)) {
                    System.err.println("The introduced tokens are invalid");
                }
                Map.Entry<String, String> key = new AbstractMap.SimpleImmutableEntry<>(s1, x);
                this.transitions.computeIfAbsent(key, k -> new LinkedList<>());
                this.transitions.get(key).add(s2);
                index++;
            }
        } catch (IOException ioException) {
            throw new RuntimeException(String.format("An error appeared while opening the file %s", faFileName), ioException);
        }
    }

    private boolean isDeterministic() {
        return this.transitions.entrySet().stream().allMatch(transition -> transition.getValue().size() == 1);
    }
}
