package fixtures;

enum Color {
    RED(255, 0, 0), GREEN(0, 255, 0), BLACK;

    private final int rgb;

    Color(int r, int g, int b) {
        this.rgb = (r << 16) | (g << 8) | b;
    }

    private Color() {
        this(0, 0, 0);
    }

    public String hex() {
        return Integer.toHexString(rgb);
    }

    void print() {
    }
}
