package fixtures;

class Report {
    String render() {
        return render(0);
    }

    String render(int indent) {
        return " ".repeat(indent);
    }

    String render(int indent, int width) {
        return render(indent);
    }

    int width() {
        return 80;
    }

    int height() {
        return 24;
    }
}
