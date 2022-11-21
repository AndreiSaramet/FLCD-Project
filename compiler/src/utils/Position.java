package utils;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        Position position = (Position) o;
        return hashPos == position.hashPos && linkedListPos == position.linkedListPos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashPos, linkedListPos);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", this.hashPos, this.linkedListPos);
    }
}
