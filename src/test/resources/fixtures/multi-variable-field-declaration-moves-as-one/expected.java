package fixtures;

class Point {
    static int ORIGIN_X, ORIGIN_Y;

    int x = 1, y = x + 1, z[] = {x, y};

    double length() {
        return Math.sqrt(x * x + y * y);
    }
}
