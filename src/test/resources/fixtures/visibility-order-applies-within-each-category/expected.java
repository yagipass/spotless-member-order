package fixtures;

public class Account {
    public interface Listener {
    }

    private static class Ledger {
    }

    public static Account open() {
        return new Account();
    }

    protected static Account copy() {
        return new Account();
    }

    public Account() {
    }

    private Account(String id) {
    }

    public void deposit(long amount) {
    }

    protected void validate() {
    }

    void touch() {
    }

    private void audit() {
    }
}
