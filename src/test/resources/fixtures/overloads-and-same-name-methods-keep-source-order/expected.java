package fixtures;

class Formatter {
    static Formatter of() {
        return new Formatter();
    }

    static Formatter of(String pattern) {
        return new Formatter();
    }

    private final String pattern = "%s";

    String format(Object value) {
        return String.valueOf(value);
    }

    String format(int value) {
        return Integer.toString(value);
    }

    String format() {
        return pattern;
    }

    String format(String value, Object... args) {
        return String.format(value, args);
    }

    Formatter format(Formatter other) {
        return other;
    }
}
