package fixtures;

public class Outer {
    static class StaticNested {
        class Deeper {
            int deepField;

            void deepMethod() {
            }
        }

        int nestedField;

        void nestedMethod() {
        }
    }

    class Inner {
        static final int CONSTANT = 1;

        Inner() {
        }
    }

    int outerField;

    void outerMethod() {
    }
}
