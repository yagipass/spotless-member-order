package fixtures;

class Service {
    interface Listener {
    }

    private static class Cache {
    }

    public static final int TIMEOUT = 10;

    private final String name = "service";

    public Service() {
    }

    private Service(String name) {
    }

    public static Service create() {
        return new Service();
    }

    static int count() {
        return 0;
    }

    void stop() {
    }

    protected void start() {
    }

    public void run() {
    }

    private static String normalize(String name) {
        return name.strip();
    }

    private void log(String message) {
    }
}
