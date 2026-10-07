package fixtures;

class Unicode {
    private String 名前 = "値";

    void grüßen() {
        System.out.println("こんにちは 👋 " + 名前);
    }

    \u0070ublic void escapedPublic() {
        String s = "\u00e9\t\u2603";
    }

    static final String CAFÉ = "caf\u00e9";

    int \u00e9t\u00e9;
}
