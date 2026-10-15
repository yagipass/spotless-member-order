package fixtures;

class Report {
    String render() {
        return render(0);
    }

    int width() {
        return 80;
    }

    String render(int indent) {
        return " ".repeat(indent);
    }

    int height() {
        return 24;
    }

    String render(int indent, int width) {
        return render(indent);
    }
}
