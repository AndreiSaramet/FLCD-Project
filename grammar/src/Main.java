import service.Service;

public class Main {
    public static void main(String[] args) {
        Service service = new Service("grammar.in", "->");
        service.run();
    }
}