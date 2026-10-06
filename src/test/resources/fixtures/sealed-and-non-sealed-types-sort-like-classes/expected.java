package fixtures;

public sealed interface Shape permits Shape.Circle, Shape.Square, Shape.Polygon {
    final class Circle implements Shape {
        private final double radius = UNIT;

        public double area() {
            return Math.PI * radius * radius;
        }
    }

    non-sealed class Square implements Shape {
        double side;

        public double area() {
            return side * side;
        }
    }

    sealed class Polygon implements Shape permits Polygon.Triangle {
        static final class Triangle extends Polygon {
        }

        public double area() {
            return 0;
        }
    }

    double UNIT = 1.0;

    double area();
}
