package fixtures;

class TrailingSemicolon {
    void method() {
    };

    int field;
}

class EmptyDeclaration {
    void method() {
    }

    ;

    int field;
}

class DoubleSemicolon {
    void method() {
    }

    int field;;
}
