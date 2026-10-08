package fixtures;

class Counter {
    static {
        created = 0;
    }

    {
        value = 1;
        created++;
    }

    static int created;

    int value;

    Counter() {
    }
}
