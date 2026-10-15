package fixtures;

final class Money {
    private final long cents;

    Money(long cents) {
        this.cents = cents;
    }

    @Override
    public int hashCode() {
        return hashCode(cents);
    }

    static int hashCode(long cents) {
        return Long.hashCode(cents);
    }

    static Money of(long cents) {
        return new Money(cents);
    }
}
