package fixtures;

public class Connection {
    public Connection(String host, int port, boolean secure) {
        this.host = host;
    }

    void close() {
    }

    Connection(String host) {
        this(host, 80, false);
    }

    private final String host;

    public Connection() {
        this("localhost");
    }

    private Connection(int port) {
        this("localhost", port, false);
    }
}
