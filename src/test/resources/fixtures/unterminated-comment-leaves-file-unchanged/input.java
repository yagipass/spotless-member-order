package fixtures;

class UnterminatedComment {
    void method() {
    }

    int field;

    /* this comment never ends, so JDT drops every member after it

    void other() {
    }
}
