package fixtures;

import java.util.Objects;

public record Range<T extends Comparable<? super T>>(T low, T high) implements Comparable<Range<T>> {
    record Bound(int value) {
        static Bound zero() {
            return new Bound(0);
        }

        Bound(int value) {
            this.value = Math.max(0, value);
        }
    }

    private static final String SEPARATOR = "..";

    static {
        System.out.println(SEPARATOR);
    }

    static <U extends Comparable<? super U>> Range<U> of(U low, U high) {
        return new Range<>(low, high);
    }

    public Range {
        Objects.requireNonNull(low);
        Objects.requireNonNull(high);
    }

    public Range(T single) {
        this(single, single);
    }

    @Override
    public int compareTo(Range<T> other) {
        return low.compareTo(other.low);
    }
}
