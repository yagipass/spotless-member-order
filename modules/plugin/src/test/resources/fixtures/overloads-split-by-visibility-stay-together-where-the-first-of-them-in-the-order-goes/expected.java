package fixtures;

class Parser {
    public int parse(String text) {
        return parse(normalize(text), 10);
    }

    private int parse(String text, int radix) {
        return Integer.parseInt(text, radix);
    }

    public boolean accepts(String text) {
        return !text.isBlank();
    }

    private String normalize(String text) {
        return text.strip();
    }
}
