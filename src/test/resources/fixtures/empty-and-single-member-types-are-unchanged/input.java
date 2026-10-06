package fixtures;

class Empty {
}

class EmptyOnOneLine {}

interface Marker {
}

enum NoConstants {
}

enum OnlyConstants {
    A, B, C
}

record Unit() {
}

class SingleField {
    private final int value = 1;
}

class SingleMethod {
    void run() {
    }
}

@interface Tag {
}
