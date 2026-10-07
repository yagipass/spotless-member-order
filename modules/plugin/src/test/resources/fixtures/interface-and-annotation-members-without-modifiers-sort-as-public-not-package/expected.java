package fixtures;

interface Api {
    @interface Marker {
        String name();

        public int value();
    }

    void run();

    private void helper() {
    }
}
