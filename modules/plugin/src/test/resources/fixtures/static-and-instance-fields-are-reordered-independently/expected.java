package fixtures;

class Registry {
    public static final String NAME = "registry";

    private static final int CAPACITY = 16;

    static {
        System.out.println("registry");
    }

    private final int size;

    {
        System.out.println("instance");
    }

    protected int limit = 1;

    Registry() {
        size = 0;
    }
}
