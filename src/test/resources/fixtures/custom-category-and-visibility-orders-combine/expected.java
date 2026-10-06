package fixtures;

class Widget {
    private Widget(int width) {
        this.width = width;
    }

    public Widget() {
    }

    private void layout() {
    }

    protected void invalidate() {
    }

    public void draw() {
    }

    private static Widget empty() {
        return new Widget();
    }

    static Widget of(int width) {
        return new Widget(width);
    }

    public static final String KIND = "widget";

    private int width;

    private static class Cache {
    }

    public static class Builder {
    }
}
