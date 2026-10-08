package fixtures;

class Configuration {
    void load() {
    }

    private int retries = defaultRetries();

    public static final String NAME;

    static {
        NAME = System.getProperty("app.name", "app");
    }

    protected int timeout = retries * 1000;

    private static final String DISPLAY_NAME = NAME.toUpperCase();

    {
        timeout += 1;
    }

    public String label = "x";

    Configuration() {
    }

    static int defaultRetries() {
        return 3;
    }
}
