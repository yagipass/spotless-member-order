package fixtures;

class Counter {
    static int created;

    static {
        created = 0;
    }

    int value;

    {
        value = 1;
        created++;
    }

    Counter() {
    }
}
