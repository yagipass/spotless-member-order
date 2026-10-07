package fixtures;

interface Validator {
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    boolean validate(String value);

    private static String trim(String value) {
        return value.trim();
    }

    default boolean validateTrimmed(String value) {
        return !isBlank(value) && validate(trim(value));
    }

    static Validator notBlank() {
        return value -> !value.isBlank();
    }
}
