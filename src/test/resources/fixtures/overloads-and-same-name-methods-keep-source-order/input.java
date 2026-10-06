package fixtures;

class Formatter {
    String format(Object value) {
        return String.valueOf(value);
    }

    static Formatter of() {
        return new Formatter();
    }

    String format(int value) {
        return Integer.toString(value);
    }

    private final String pattern = "%s";

    String format() {
        return pattern;
    }

    static Formatter of(String pattern) {
        return new Formatter();
    }

    String format(String value, Object... args) {
        return String.format(value, args);
    }

    Formatter format(Formatter other) {
        return other;
    }
}
