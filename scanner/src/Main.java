import symbol_table.SymbolTable;
import symbol_table.SymbolTableImpl;

public class Main {
    private static void insert(SymbolTable st, String value) {
        System.out.println(value + " - " + st.insert(value));
        System.out.println(st);
    }

    public static void main(String[] args) {
        SymbolTable st = new SymbolTableImpl();
        System.out.println(st);
        insert(st, "abc");
        insert(st, "bac");
        insert(st, "bbb");
        insert(st, "bac");
        insert(st, "\"Hello World!\"");
    }
}
