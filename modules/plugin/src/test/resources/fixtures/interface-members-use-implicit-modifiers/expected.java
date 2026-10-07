package fixtures;

public interface Repository<T> {
    interface Listener {
        void changed();
    }

    enum Mode {
        READ, WRITE
    }

    String DEFAULT_ID = "default";

    public static final int LIMIT = 100;

    static <T> Repository<T> empty() {
        return id -> null;
    }

    default T findOrDefault(String id, T fallback) {
        T found = find(id);
        return found != null ? found : fallback;
    }

    T find(String id);
}
