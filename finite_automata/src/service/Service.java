package service;

import finite_automaton.impl.FiniteAutomatonImpl;

import java.util.*;
import java.util.stream.Collectors;

public class Service {
    private final FiniteAutomatonImpl finiteAutomaton;

    private final List<Map.Entry<String, Runnable>> options;

    public Service(String faFileName) {
        this.finiteAutomaton = new FiniteAutomatonImpl(faFileName);
        this.options = new ArrayList<>();
        this.setup();
    }

    private void setup() {
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Print the states of the automaton", this::printStates));
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Print the alphabet of the automaton", this::printAlphabet));
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Print the initial state of the automaton", this::printInitialState));
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Print the final states of the automaton", this::printFinalStates));
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Print the transitions of the automaton", this::printTransitions));
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Check if a sequence is accepted by the automaton", this::checkSequence));
    }

    public void run() {
        while (true) {
            this.printMenu();
            int option = this.readOption();
            if (option == 0) {
                System.out.println("Goodbye!");
                return;
            }
            this.options.get(option - 1).getValue().run();
        }
    }

    private void printMenu() {
        System.out.printf("Menu.\n%s\n", "0. Exit the application\n" + this.options.stream().map(option -> String.format("%d. %s", this.options.indexOf(option) + 1, option.getKey())).collect(Collectors.joining("\n")));
    }

    private int readOption() {
        System.out.print("Your option: ");
        try {
            int option = new Scanner(System.in).nextInt();
            if (option > this.options.size()) {
                System.out.println("You introduced an invalid option\nTry again!");
                return this.readOption();
            }
            return option;
        } catch (InputMismatchException exception) {
            System.out.println("You introduced an invalid option\nTry again!");
            return this.readOption();
        }
    }

    private void printStates() {
        System.out.printf("The states of the finite automata are: %s\n", String.join(", ", this.finiteAutomaton.getStates()));
    }

    private void printAlphabet() {
        System.out.printf("The alphabet of the finite automata is: %s\n", String.join(", ", this.finiteAutomaton.getAlphabet()));
    }

    private void printInitialState() {
        System.out.printf("The initial state of the finite automata is: %s\n", this.finiteAutomaton.getInitialState());
    }

    private void printFinalStates() {
        System.out.printf("The final states of the finite automata are: %s\n", String.join(", ", this.finiteAutomaton.getFinalStates()));
    }

    private void printTransitions() {
        System.out.println(this.finiteAutomaton.getTransitions().entrySet().stream().flatMap(transition -> transition.getValue().stream().map(result -> String.format("(%s, %s) -> %s", transition.getKey().getKey(), transition.getKey().getValue(), result))).collect(Collectors.joining("\n")));
    }

    private String readSequence() {
        System.out.print("Introduce the sequence: ");
        return new Scanner(System.in).nextLine();
    }

    private void checkSequence() {
        String sequence = this.readSequence();
        String isAccepted = "";
        if (!this.finiteAutomaton.accepts(sequence)) {
            isAccepted = "NOT ";
        }
        System.out.printf("The word %s is %saccepted by the finite automaton\n", sequence, isAccepted);
    }
}
