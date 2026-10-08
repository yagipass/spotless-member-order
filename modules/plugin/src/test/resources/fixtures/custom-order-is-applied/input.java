package fixtures;

class Inventory {
    static Inventory empty() {
        return new Inventory(0);
    }

    record Item(String name) {
    }

    int count() {
        return count;
    }

    Inventory(int count) {
        this.count = count;
    }

    static final String UNIT = "pcs";

    private final int count;
}
