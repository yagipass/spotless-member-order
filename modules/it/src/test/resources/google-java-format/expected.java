package com.example;

import java.util.ArrayList;
import java.util.List;

public class Example {

  record Entry(String message) {}

  static final int LIMIT = 10;

  public static final List<String> NAMES = new ArrayList<>();

  static {
    NAMES.add("example");
  }

  public static Example create() {
    return new Example("example: ");
  }

  private final List<String> messages = new ArrayList<>();

  // Prepended to every message.
  public final String prefix;

  public Example(String prefix) {
    this.prefix = prefix;
  }

  private void log(String message) {
    messages.add(prefix + message);
  }

  /** Logs that the example ran. */
  public void run() {
    log("run");
  }

  protected int size() {
    return messages.size();
  }
}
