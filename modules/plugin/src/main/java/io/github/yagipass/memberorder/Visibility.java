package io.github.yagipass.memberorder;

enum Visibility {
  PUBLIC("B"),
  PROTECTED("R"),
  PACKAGE("D"),
  PRIVATE("V");

  private final String code;

  Visibility(String code) {
    this.code = code;
  }

  String code() {
    return code;
  }
}
