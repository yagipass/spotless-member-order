package fixtures;

class Widget {
    public static final String KIND = "widget";

    private int width;

    public Widget() {
    }

    private Widget(int width) {
        this.width = width;
    }

    public void draw() {
    }

    private void layout() {
    }

    protected void invalidate() {
    }

    static Widget of(int width) {
        return new Widget(width);
    }

    private static Widget empty() {
        return new Widget();
    }

    public static class Builder {
    }

    private static class Cache {
    }
}
