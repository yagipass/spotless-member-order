package fixtures;

sealed interface Shape permits Shape.Circle {
    double area();

    record Circle(double r) implements Shape {
        public double area() {
            return Math.PI * r * r;
        }
    }
    // Circle must stay the only permitted subtype
}
