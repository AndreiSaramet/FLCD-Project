package utils;

/**
 * This class represents an entry in the program internal form, i.e., a pair of the form (token, position_in_the_symbol_table)
 */
public class PIFEntry {
    //    the identified token
    public final String token;
    //    the position of the token in the symbol table, i.e., a pair of the form (hashPosition, linkedListPosition)
    public final Position stPosition;

    public PIFEntry(String token, Position stPosition) {
        this.token = token;
        this.stPosition = stPosition;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", this.token, this.stPosition.toString());
    }
}
