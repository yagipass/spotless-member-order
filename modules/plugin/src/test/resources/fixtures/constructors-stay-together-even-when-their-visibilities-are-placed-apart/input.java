package fixtures;

class Connection {
    private Connection(int port) {
        this("localhost", port);
    }

    void close() {
    }

    Connection(String host, int port) {
        this.host = host;
    }

    private final String host;
}
