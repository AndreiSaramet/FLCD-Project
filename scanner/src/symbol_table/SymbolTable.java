package symbol_table;


/**
 * Symbol Table interface with the only method 'insert'
 */
public interface SymbolTable {
    /**
     * This method adds the given value in the symbol table, or returns its current position if the value
     * already is in the table
     *
     * @param value an identifier/constant
     * @return the position of the element in the table
     */
    Position insert(String value);
}
