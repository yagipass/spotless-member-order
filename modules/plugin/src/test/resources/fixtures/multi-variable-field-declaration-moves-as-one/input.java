package fixtures;

class Point {
    double length() {
        return Math.sqrt(x * x + y * y);
    }

    int x = 1, y = x + 1, z[] = {x, y};

    static int ORIGIN_X, ORIGIN_Y;
}
