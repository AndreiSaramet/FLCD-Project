package service;

import grammar.Grammar;

import java.util.*;
import java.util.stream.Collectors;

public class Service {
    private final Grammar grammar;

    private final List<Map.Entry<String, Runnable>> options;

    private final String delimiter;

    public Service(String fileName, String delimiter) {
        this.grammar = new Grammar(fileName);
        this.options = new ArrayList<>();
        this.delimiter = delimiter;
        this.setup();
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

    private void printMenu() {
        System.out.printf("Menu.\n%s\n%s\n", "0. Exit the application", this.options.stream().map(option -> String.format("%d. %s", this.options.indexOf(option) + 1, option.getKey())).collect(Collectors.joining("\n")));
    }

    private void setup() {
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Print the set of nonterminals of the grammar", this::printNonterminals));
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Print the set of terminals of the grammar", this::printTerminals));
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Print the start symbol of the grammar", this::printStartSymbol));
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Print the set of productions of the grammar", this::printProductions));
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Print the productions for a given nonterminal", this::printProductionsForNonterminal));
        this.options.add(new AbstractMap.SimpleImmutableEntry<>("Check if the grammar is context free", this::checkCFG));
    }

    private void printNonterminals() {
        System.out.printf("The nonterminal symbols of the grammar are:\n%s\n", String.join("\n", this.grammar.getNonterminalSymbols()));
    }

    private void printTerminals() {
        System.out.printf("The terminal symbols of the grammar are:\n%s\n", String.join("\n", this.grammar.getTerminalSymbols()));
    }

    private void printStartSymbol() {
        System.out.printf("The start symbol of the grammar is %s\n", this.grammar.getStartSymbol());
    }

    private String readNonterminal() {
        System.out.print("Introduce the nonterminal: ");
        return new Scanner(System.in).nextLine();
    }

    private void printProductionsForNonterminal() {
        String nonterminal = this.readNonterminal();
        if (!this.grammar.getNonterminalSymbols().contains(nonterminal)) {
            System.out.println("The given nonterminal does not belong to the grammar");
            return;
        }
        System.out.printf("The productions for the nonterminal %s are:\n%s\n", nonterminal, this.grammar.getProductionsForNonterminal(nonterminal).entrySet().stream().flatMap(production -> production.getValue().stream().map(lhs -> String.format("%s " + this.delimiter + " %s", String.join(" ", production.getKey()), String.join(" ", lhs)))).collect(Collectors.joining("\n")));
    }

    private void printProductions() {
        System.out.println(this.grammar.getProductions().entrySet().stream().flatMap(production -> production.getValue().stream().map(lhs -> String.format("%s " + this.delimiter + " %s", String.join(" ", production.getKey()), String.join(" ", lhs)))).collect(Collectors.joining("\n")));
    }

    private void checkCFG() {
        System.out.println(this.grammar.isContextFree() ? "The grammar is context free" : "The grammar is NOT context free");
    }
}
