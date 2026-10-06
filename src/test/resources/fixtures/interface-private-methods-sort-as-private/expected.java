package fixtures;

interface Validator {
    static Validator notBlank() {
        return value -> !value.isBlank();
    }

    private static String trim(String value) {
        return value.trim();
    }

    boolean validate(String value);

    default boolean validateTrimmed(String value) {
        return !isBlank(value) && validate(trim(value));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
