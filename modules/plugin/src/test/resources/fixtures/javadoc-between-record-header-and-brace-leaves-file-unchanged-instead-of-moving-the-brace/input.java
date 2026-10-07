package fixtures;

record Point(int x) implements Runnable /** Runs the point. */ {
    public void run() {
    }

    static int count;
}
