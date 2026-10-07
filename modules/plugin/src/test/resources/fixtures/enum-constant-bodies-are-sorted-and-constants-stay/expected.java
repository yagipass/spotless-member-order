package fixtures;

enum Operation {
    PLUS("+") {
        private final String description = "addition";

        @Override
        int apply(int a, int b) {
            return a + b;
        }
    },
    TIMES("*", new Object() {
        int precedence = 2;

        @Override
        public String toString() {
            return "times";
        }
    }) {
        @Override
        int apply(int a, int b) {
            return a * b;
        }
    },
    ABS("|") { // trailing comment after the constant body brace
        static final int ARITY = 1;

        @Override
        int apply(int a, int b) {
            return Math.abs(a);
        }
    };

    static Operation parse(String symbol) {
        return PLUS;
    }

    private final String symbol;

    Operation(String symbol) {
        this(symbol, null);
    }

    Operation(String symbol, Object detail) {
        this.symbol = symbol;
    }

    abstract int apply(int a, int b);
}

enum Level {
    LOW, // the default
    HIGH; // escalated

    static Level max() {
        return HIGH;
    }

    private int weight;
}
