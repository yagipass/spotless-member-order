package fixtures;

class UnterminatedString {
    void method() {
    }

    int field;

    String text = "never closed, so JDT stretches this field to the end of the file;
}
