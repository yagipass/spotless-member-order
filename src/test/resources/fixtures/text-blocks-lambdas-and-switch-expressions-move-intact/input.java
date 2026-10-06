package fixtures;

import module java.base;

class Expressions {
    String describe(Object value) {
        return switch (value) {
            case Integer i when i > 0 -> "positive";
            case String s -> {
                yield """
                    text with } and { braces,
                    // something that looks like a comment
                    /* and a block comment */
                    """;
            }
            default -> "other";
        };
    }

    Expressions(int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        super();
    }

    Function<String, Integer> parser = text -> {
        int _ = 0;
        return Integer.parseInt(text);
    };

    static final String TEMPLATE = """
        }
        """;
}
