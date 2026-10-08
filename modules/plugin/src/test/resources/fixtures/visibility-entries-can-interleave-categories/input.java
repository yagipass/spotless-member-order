package fixtures;

class Service {
    private static String normalize(String name) {
        return name.strip();
    }

    void stop() {
    }

    private void log(String message) {
    }

    public static Service create() {
        return new Service();
    }

    private static class Cache {
    }

    private final String name = "service";

    protected void start() {
    }

    public static final int TIMEOUT = 10;

    private Service(String name) {
    }

    public Service() {
    }

    static int count() {
        return 0;
    }

    public void run() {
    }

    interface Listener {
    }
}
