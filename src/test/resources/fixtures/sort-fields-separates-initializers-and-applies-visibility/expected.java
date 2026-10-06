package fixtures;

class Settings {
    public static final int MIN = 0;

    private static final int MAX = 10;

    static {
        System.out.println("loading");
    }

    static {
        System.out.println("loaded");
    }

    public String label;

    protected int size;

    private String name;

    {
        System.out.println("instance");
    }
}
