package fixtures;

class Configuration {
    public static final String NAME;

    static {
        NAME = System.getProperty("app.name", "app");
    }

    private static final String DISPLAY_NAME = NAME.toUpperCase();

    static int defaultRetries() {
        return 3;
    }

    private int retries = defaultRetries();

    protected int timeout = retries * 1000;

    {
        timeout += 1;
    }

    public String label = "x";

    Configuration() {
    }

    void load() {
    }
}
