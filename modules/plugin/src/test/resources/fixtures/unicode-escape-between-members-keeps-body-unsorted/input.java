package fixtures;

class EscapedBlankLine {
    void method() {
    }
\u0020
    int field;
}

class EscapedLineBreakInComment {
    void method() {
    } // the escape ends this comment \u000a int hidden;

    int field;
}
