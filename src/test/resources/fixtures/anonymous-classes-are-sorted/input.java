package fixtures;

import java.util.Comparator;

class Anonymous {
    void register() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Comparator<String> nested = new Comparator<>() {
                    @Override
                    public int compare(String a, String b) {
                        return a.compareTo(b);
                    }

                    private final int bias = 0;
                };
            }

            private int runs;
        });
    }

    Runnable field = new Runnable() {
        @Override
        public void run() {
        }

        static final String NAME = "field";
    };

    Runnable lambda = () -> {
        Object o = new Object() {
            @Override
            public String toString() {
                return "lambda";
            }

            int hash;
        };
    };
}
