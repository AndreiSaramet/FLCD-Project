package program_internal_form;

import utils.Position;

/**
 * Program Internal Form interface with only one method 'insert'
 */
public interface ProgramInternalForm {
    /**
     * This method adds a pair (token, st_position) in the program internal form
     *
     * @param token      the identified token
     * @param stPosition the position of the identifier/constant in the symbol table, or an invalid position for reserved words, operators and separators
     */
    void insert(String token, Position stPosition);
}
