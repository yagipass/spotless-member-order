package fixtures;

import java.io.IOException;
import java.util.List;
import java.util.Map;

abstract class Converter<S, T extends Comparable<? super T>> {
    @SafeVarargs
    final <R extends T> List<R> convertAll(S... sources) throws IOException, InterruptedException {
        return List.of();
    }

    <K, V extends Map<K, ? extends List<? super T>>> Converter(K key, V value) {
    }

    final public static <E> E identity(E element) {
        return element;
    }

    protected abstract T convert(S source) throws IOException;

    private Map<String, List<Map<S, T>>> cache;

    synchronized native void nativeHook();
}
