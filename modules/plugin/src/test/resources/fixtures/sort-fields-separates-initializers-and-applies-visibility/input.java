package fixtures;

class Settings {
    static {
        System.out.println("loading");
    }

    private static final int MAX = 10;

    {
        System.out.println("instance");
    }

    private String name;

    public static final int MIN = 0;

    protected int size;

    public String label;

    static {
        System.out.println("loaded");
    }
}
