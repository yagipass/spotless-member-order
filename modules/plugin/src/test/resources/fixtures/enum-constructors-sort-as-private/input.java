package fixtures;

enum Color {
    RED(255, 0, 0), GREEN(0, 255, 0), BLACK;

    Color(int r, int g, int b) {
        this.rgb = (r << 16) | (g << 8) | b;
    }

    public String hex() {
        return Integer.toHexString(rgb);
    }

    private Color() {
        this(0, 0, 0);
    }

    private final int rgb;

    void print() {
    }
}
