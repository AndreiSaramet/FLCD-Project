import scanner.MyScanner;

public class Main {
    private static void scanFile(MyScanner scanner, String sourceCodeFileName, String stFileName, String pifFileName) {
        System.out.println(sourceCodeFileName);
        scanner.scan(sourceCodeFileName, stFileName, pifFileName);
    }

    public static void main(String[] args) {
        MyScanner scanner = new MyScanner("token.in", "-----", "idFa.in", "constIntFa.in", "constCharFa.in", "constStrFa.in");
        scanFile(scanner, "p1.txt", "p1_st.out", "p1_pif.out");
        scanFile(scanner, "p2.txt", "p2_st.out", "p2_pif.out");
        scanFile(scanner, "p2err.txt", "p2err_st.out", "p2err_pif.out");
        scanFile(scanner, "p3.txt", "p3_st.out", "p3_pif.out");
    }
}
