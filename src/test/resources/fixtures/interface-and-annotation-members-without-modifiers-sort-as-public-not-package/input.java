package fixtures;

interface Api {
    private void helper() {
    }

    void run();

    @interface Marker {
        String name();

        public int value();
    }
}
