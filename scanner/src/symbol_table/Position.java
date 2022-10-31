package symbol_table;

/**
 * This class represents a position in a hash table with separate chaining
 */
public class Position {
    //    the actual position in the hash table
    public final int hashPos;
    //    the position in the linked list associated with a slot of the hash table
    public final int linkedListPos;

    public Position(int hashPos, int linkedListPos) {
        this.hashPos = hashPos;
        this.linkedListPos = linkedListPos;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", this.hashPos, this.linkedListPos);
    }
}
