package fixtures;

public class Connection {
    private final String host;

    public Connection(String host, int port, boolean secure) {
        this.host = host;
    }

    Connection(String host) {
        this(host, 80, false);
    }

    public Connection() {
        this("localhost");
    }

    private Connection(int port) {
        this("localhost", port, false);
    }

    void close() {
    }
}
