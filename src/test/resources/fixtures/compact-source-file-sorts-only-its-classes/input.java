import java.util.List;

// greets everyone
void greet(List<String> names) {
    names.forEach(name -> System.out.println(new Greeting(name).text()));
}

static final String PREFIX = "Hello, ";

void main() {
    greet(List.of("Ada", "Linus"));
}

record Greeting(String name) {
    String text() {
        return PREFIX + name;
    }

    static final String SUFFIX = "!";
}

class Counter {
    void increment() {
        count++;
    }

    private int count;
}
