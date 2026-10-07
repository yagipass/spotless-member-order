package fixtures;

class ClosingBrace {
    void method() {
    } // moving this member last would comment out the closing brace
    int field; }

class Other {
    int field;

    void method() {
    }
}
