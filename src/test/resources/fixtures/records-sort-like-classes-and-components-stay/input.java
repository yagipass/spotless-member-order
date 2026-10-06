package fixtures;

import java.util.Objects;

public record Range<T extends Comparable<? super T>>(T low, T high) implements Comparable<Range<T>> {
    @Override
    public int compareTo(Range<T> other) {
        return low.compareTo(other.low);
    }

    public Range {
        Objects.requireNonNull(low);
        Objects.requireNonNull(high);
    }

    private static final String SEPARATOR = "..";

    public Range(T single) {
        this(single, single);
    }

    static <U extends Comparable<? super U>> Range<U> of(U low, U high) {
        return new Range<>(low, high);
    }

    record Bound(int value) {
        Bound(int value) {
            this.value = Math.max(0, value);
        }

        static Bound zero() {
            return new Bound(0);
        }
    }

    static {
        System.out.println(SEPARATOR);
    }
}
