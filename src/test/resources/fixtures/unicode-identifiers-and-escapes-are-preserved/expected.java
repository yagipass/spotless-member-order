package fixtures;

class Unicode {
    static final String CAFÉ = "caf\u00e9";

    private String 名前 = "値";

    int \u00e9t\u00e9;

    \u0070ublic void escapedPublic() {
        String s = "\u00e9\t\u2603";
    }

    void grüßen() {
        System.out.println("こんにちは 👋 " + 名前);
    }
}
