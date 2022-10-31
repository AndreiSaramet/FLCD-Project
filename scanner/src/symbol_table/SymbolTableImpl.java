package symbol_table;

/**
 * The symbol table implemented on a hash table with a fixed size
 */
public class SymbolTableImpl implements SymbolTable {
    //    the fixed size of the hash table
    private final int m = 47;

    //    the hash table - an array of linked lists (the head of each linked list)
    private final Node[] hashTable = new Node[this.m];

    public SymbolTableImpl() {

    }

    @Override
    public Position insert(String value) {
        Position pos = this.search(value);
        if (pos == null) {
            return this.add(value);
        }
        return pos;
    }

    /**
     * This method adds a value in the hash table. As a precondition the value must not be already in the hash table.
     *
     * @param value the value to be added
     * @return the position where the value was added
     */
    private Position add(String value) {
        int pos = this.hash(value);
        Node newNode = new Node();
        newNode.value = value;
        newNode.next = null;
//        if the slot of the hash table is empty, a new linked list (its head) should be created
        if (this.hashTable[pos] == null) {
            this.hashTable[pos] = newNode;
            return new Position(pos, 0);
        }
//        otherwise, the linked list is parsed and at is end the value is appended
        int counter = 1;
        Node node = this.hashTable[pos];
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
        Node node = this.hashTable[pos];
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

    @Override
    public String toString() {
        StringBuilder symbolTable = new StringBuilder();
        int pos;
        for (pos = 0; pos < this.m; pos++) {
            if (this.hashTable[pos] == null) {
                symbolTable.append(pos).append(" - \n");
            } else {
                StringBuilder elements = new StringBuilder();
                Node node = this.hashTable[pos];
                while (node != null) {
                    elements.append(node.value).append(" -> ");
                    node = node.next;
                }
                elements.delete(elements.lastIndexOf("-"), elements.lastIndexOf("->") + 1);
                symbolTable.append(pos).append(" ").append(elements).append("\n");
            }
        }
        return symbolTable.toString();
    }
}