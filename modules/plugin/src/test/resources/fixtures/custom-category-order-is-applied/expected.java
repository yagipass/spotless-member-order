package fixtures;

class Inventory {
    static final String UNIT = "pcs";

    private final int count;

    Inventory(int count) {
        this.count = count;
    }

    int count() {
        return count;
    }

    static Inventory empty() {
        return new Inventory(0);
    }

    record Item(String name) {
    }
}
