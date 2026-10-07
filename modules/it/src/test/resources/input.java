package com.example;

import java.util.ArrayList;
import java.util.List;

public class Example {

    private final List<String> messages = new ArrayList<>();

    public static Example create() {
        return new Example("example: ");
    }

    private void log(String message) {
        messages.add(prefix + message);
    }

    // Prepended to every message.
    public final String prefix;

    static final int LIMIT = 10;

    /** Logs that the example ran. */
    public void run() {
        log("run");
    }

    record Entry(String message) {
    }

    public static final List<String> NAMES = new ArrayList<>();

    public Example(String prefix) {
        this.prefix = prefix;
    }

    static {
        NAMES.add("example");
    }

    protected int size() {
        return messages.size();
    }
}
