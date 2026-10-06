package io.github.yagipass.memberorder;

enum Category {
  TYPE("T"),
  STATIC_FIELD("SF"),
  STATIC_INITIALIZER("SI"),
  STATIC_METHOD("SM"),
  FIELD("F"),
  INITIALIZER("I"),
  CONSTRUCTOR("C"),
  METHOD("M");

  private final String code;

  Category(String code) {
    this.code = code;
  }

  String code() {
    return code;
  }
}
