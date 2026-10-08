package fixtures;

class Registry {
    private static final int CAPACITY = 16;

    private final int size;

    static {
        System.out.println("registry");
    }

    {
        System.out.println("instance");
    }

    public static final String NAME = "registry";

    protected int limit = 1;

    Registry() {
        size = 0;
    }
}
