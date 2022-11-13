package program_internal_form.impl;

import program_internal_form.ProgramInternalForm;
import utils.PIFEntry;
import utils.PIFNode;
import utils.Position;

/**
 * The program internal form implemented on a singly linked list with dynamic allocation
 */
public class ProgramInternalFormImpl implements ProgramInternalForm {
    //    the head of the linked list
    private PIFNode head = null;
    //    the tail of the linked list, kept such that the insertion is done in constant time
    private PIFNode tail = null;

    /**
     * This method returns a string representing the internal implementation details of the program internal form
     *
     * @return a string
     */
    static public String implementationDetails() {
        return "The Program Internal Form is implemented on a singly linked list with dynamic allocation";
    }

    public ProgramInternalFormImpl() {
    }

    /**
     * This method inserts a new pair of type (token, stPosition) at the end of the list
     *
     * @param token      the identified token
     * @param stPosition the position of the identifier/constant in the symbol table, or the pair (-1, -1) for reserved words, operators and separators
     */
    @Override
    public void insert(String token, Position stPosition) {
//        a new node is created
        PIFNode newNode = new PIFNode();
        newNode.value = new PIFEntry(token, stPosition);
        newNode.next = null;
//        if the list is empty, the new node is the head of the list, otherwise it is appended at its end
        if (this.head == null) {
            this.head = newNode;
        } else {
            this.tail.next = newNode;
        }
//        finally, the newNode replaces the old tail
        this.tail = newNode;
    }

    @Override
    public String toString() {
        StringBuilder programInternalForm = new StringBuilder();
        PIFNode node = this.head;
        while (node != null) {
            programInternalForm.append(node.value.token).append(" <-> ").append(node.value.stPosition).append("\n");
            node = node.next;
        }
        return programInternalForm.toString();
    }
}
