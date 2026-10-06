package com.example;

import java.util.ArrayList;
import java.util.List;

public class Example {

    public static final List<String> NAMES = new ArrayList<>();

    static final int LIMIT = 10;

    static {
        NAMES.add("example");
    }

    // Prepended to every message.
    public final String prefix;

    private final List<String> messages = new ArrayList<>();

    public Example(String prefix) {
        this.prefix = prefix;
    }

    public static Example create() {
        return new Example("example: ");
    }

    /** Logs that the example ran. */
    public void run() {
        log("run");
    }

    protected int size() {
        return messages.size();
    }

    private void log(String message) {
        messages.add(prefix + message);
    }

    record Entry(String message) {
    }
}
