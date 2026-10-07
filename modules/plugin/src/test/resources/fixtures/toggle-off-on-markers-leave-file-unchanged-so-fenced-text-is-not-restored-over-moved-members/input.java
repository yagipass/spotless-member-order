package fixtures;

class Fenced {
    int instanceValue() {
        // spotless:off
        return 1;
        // spotless:on
    }

    static int staticValue() {
        // spotless:off
        return 2;
        // spotless:on
    }
}

class Unfenced {
    void method() {
    }

    int field;
}
