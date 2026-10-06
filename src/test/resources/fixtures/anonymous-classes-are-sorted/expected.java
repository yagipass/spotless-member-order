package fixtures;

import java.util.Comparator;

class Anonymous {
    Runnable field = new Runnable() {
        static final String NAME = "field";

        @Override
        public void run() {
        }
    };

    Runnable lambda = () -> {
        Object o = new Object() {
            int hash;

            @Override
            public String toString() {
                return "lambda";
            }
        };
    };

    void register() {
        Thread thread = new Thread(new Runnable() {
            private int runs;

            @Override
            public void run() {
                Comparator<String> nested = new Comparator<>() {
                    private final int bias = 0;

                    @Override
                    public int compare(String a, String b) {
                        return a.compareTo(b);
                    }
                };
            }
        });
    }
}
