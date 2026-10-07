package fixtures;

enum Operation {
    PLUS("+") {
        @Override
        int apply(int a, int b) {
            return a + b;
        }

        private final String description = "addition";
    },
    TIMES("*", new Object() {
        @Override
        public String toString() {
            return "times";
        }

        int precedence = 2;
    }) {
        @Override
        int apply(int a, int b) {
            return a * b;
        }
    },
    ABS("|") { // trailing comment after the constant body brace
        @Override
        int apply(int a, int b) {
            return Math.abs(a);
        }

        static final int ARITY = 1;
    };

    abstract int apply(int a, int b);

    Operation(String symbol) {
        this(symbol, null);
    }

    private final String symbol;

    Operation(String symbol, Object detail) {
        this.symbol = symbol;
    }

    static Operation parse(String symbol) {
        return PLUS;
    }
}

enum Level {
    LOW, // the default
    HIGH; // escalated

    private int weight;

    static Level max() {
        return HIGH;
    }
}
