package fixtures;

import java.util.ArrayList;
import java.util.List;

public class Library {
    public interface Visitor {
        void visit(Book book);
    }

    public record Book(String title, String author) {
    }

    public static final String NAME = "library";

    private static final List<Library> INSTANCES = new ArrayList<>();

    static {
        INSTANCES.clear();
    }

    public static Library create() {
        Library library = new Library();
        INSTANCES.add(library);
        return library;
    }

    private static void reset() {
        INSTANCES.clear();
    }

    private final List<Book> books = new ArrayList<>();

    {
        books.clear();
    }

    public Library() {
    }

    Library(Book first) {
        books.add(first);
    }

    public void add(Book book) {
        books.add(book);
    }

    public void accept(Visitor visitor) {
        books.forEach(visitor::visit);
    }

    protected int size() {
        return books.size();
    }

    void clear() {
        books.clear();
    }

    private void log(String message) {
        System.out.println(message);
    }
}
