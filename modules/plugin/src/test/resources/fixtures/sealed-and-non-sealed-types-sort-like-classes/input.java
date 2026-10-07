package fixtures;

public sealed interface Shape permits Shape.Circle, Shape.Square, Shape.Polygon {
    double area();

    double UNIT = 1.0;

    final class Circle implements Shape {
        public double area() {
            return Math.PI * radius * radius;
        }

        private final double radius = UNIT;
    }

    non-sealed class Square implements Shape {
        public double area() {
            return side * side;
        }

        double side;
    }

    sealed class Polygon implements Shape permits Polygon.Triangle {
        public double area() {
            return 0;
        }

        static final class Triangle extends Polygon {
        }
    }
}
