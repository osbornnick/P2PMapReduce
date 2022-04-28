package client;

/**
 * An application that starts up 5 clients, to be used in a map reduce.
 */
public class BulkClientApp {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar <hostname> <port>");
            System.exit(1);
        }
        System.out.println("Starting 5 clients");
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        for (int i = 0; i < 5; i++) {
            ClientImpl client = new ClientImpl("client_" + i, hostname, port);
        }
    }
}
