package fixtures;

class Connection {
    private final String host;

    Connection(String host, int port) {
        this.host = host;
    }

    private Connection(int port) {
        this("localhost", port);
    }

    void close() {
    }
}
