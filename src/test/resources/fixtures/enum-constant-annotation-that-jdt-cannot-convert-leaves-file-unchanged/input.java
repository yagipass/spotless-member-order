package fixtures;

enum Broken {
    A@Deprecated, B;

    void method() {
    }

    static int count;
}
