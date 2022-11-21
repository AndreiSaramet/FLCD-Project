package symbol_table.impl;

import symbol_table.SymbolTable;
import utils.STNode;
import utils.Position;

/**
 * The symbol table implemented on a hash table with a fixed size
 */
public class SymbolTableImpl implements SymbolTable {
    //    the fixed size of the hash table
    private final int m = 47;

    //    the hash table - an array of singly linked lists (the head of each linked list)
    private final STNode[] hashTable = new STNode[this.m];

    /**
     * This method returns a string representing the internal implementation details of the symbol table
     *
     * @return a string
     */
    static public String implementationDetails() {
        return "The Symbol Table is implemented on a hash table with collision resolution through separate chaining";
    }

    public SymbolTableImpl() {

    }

    @Override
    public Position insert(String symbol) {
        Position pos = this.search(symbol);
        if (pos == null) {
            return this.add(symbol);
        }
        return pos;
    }

    @Override
    public String toString() {
        StringBuilder symbolTable = new StringBuilder();
        for (STNode node : this.hashTable) {
            STNode currentNode = node;
            while (currentNode != null) {
                symbolTable.append(currentNode.value).append(" <-> ").append(this.search(currentNode.value).toString()).append("\n");
                currentNode = currentNode.next;
            }
        }
        return symbolTable.toString();
    }

    /**
     * This method adds a value in the hash table. As a precondition the value must not be already in the hash table.
     *
     * @param value the value to be added
     * @return the position where the value was added
     */
    private Position add(String value) {
        int pos = this.hash(value);
        STNode newNode = new STNode();
        newNode.value = value;
        newNode.next = null;
//        if the slot of the hash table is empty, a new linked list (its head) should be created
        if (this.hashTable[pos] == null) {
            this.hashTable[pos] = newNode;
            return new Position(pos, 0);
        }
//        otherwise, the linked list is parsed and at is end the value is appended
        int counter = 1;
        STNode node = this.hashTable[pos];
        while (node.next != null) {
            node = node.next;
            counter++;
        }
        node.next = newNode;
        return new Position(pos, counter);
    }

    /**
     * This method searches for a value in the hash table.
     *
     * @param value the value to be searched
     * @return the position, if the value is found, null otherwise
     */
    private Position search(String value) {
        int pos = this.hash(value);
//        if the hashed slot is empty, the value obviously cannot be found in the hash table
        if (this.hashTable[pos] == null) {
            return null;
        }
        STNode node = this.hashTable[pos];
        int counter = 0;
        while (node != null) {
            if (node.value.equals(value)) {
                return new Position(pos, counter);
            }
            node = node.next;
            counter++;
        }
        return null;
    }

    /**
     * This method returns the hash position of given value.
     *
     * @param value the value to be hashed
     * @return an integer between 0 and the size of the hash table, representing the slot to which the value will be hashed
     */
    private int hash(String value) {
        return value.chars().sum() % this.m;
    }
}
