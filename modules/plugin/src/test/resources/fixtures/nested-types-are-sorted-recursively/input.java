package fixtures;

public class Outer {
    void outerMethod() {
    }

    static class StaticNested {
        void nestedMethod() {
        }

        class Deeper {
            void deepMethod() {
            }

            int deepField;
        }

        int nestedField;
    }

    int outerField;

    class Inner {
        Inner() {
        }

        static final int CONSTANT = 1;
    }
}
