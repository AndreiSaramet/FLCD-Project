package symbol_table;


import utils.Position;

/**
 * Symbol Table interface with the only method 'insert'
 */
public interface SymbolTable {
    /**
     * This method adds the given symbol in the symbol table, or returns its current position if the symbol
     * already is in the table
     *
     * @param symbol an identifier/constant
     * @return the position of the element in the table
     */
    Position insert(String symbol);
}
