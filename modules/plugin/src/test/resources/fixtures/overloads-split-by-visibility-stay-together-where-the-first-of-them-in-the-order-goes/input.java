package fixtures;

class Parser {
    private int parse(String text, int radix) {
        return Integer.parseInt(text, radix);
    }

    private String normalize(String text) {
        return text.strip();
    }

    public int parse(String text) {
        return parse(normalize(text), 10);
    }

    public boolean accepts(String text) {
        return !text.isBlank();
    }
}
