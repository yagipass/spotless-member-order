package fixtures;

public interface Repository<T> {
    default T findOrDefault(String id, T fallback) {
        T found = find(id);
        return found != null ? found : fallback;
    }

    T find(String id);

    static <T> Repository<T> empty() {
        return id -> null;
    }

    String DEFAULT_ID = "default";

    interface Listener {
        void changed();
    }

    public static final int LIMIT = 100;

    enum Mode {
        READ, WRITE
    }
}
