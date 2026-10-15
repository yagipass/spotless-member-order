package fixtures;

final class Money {
    static int hashCode(long cents) {
        return Long.hashCode(cents);
    }

    @Override
    public int hashCode() {
        return hashCode(cents);
    }

    static Money of(long cents) {
        return new Money(cents);
    }

    private final long cents;

    Money(long cents) {
        this.cents = cents;
    }
}
