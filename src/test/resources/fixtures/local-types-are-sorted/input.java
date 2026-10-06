package fixtures;

import java.util.function.Supplier;

class Locals {
    void method() {
        class LocalClass {
            void run() {
            }

            int count;
        }
        record LocalRecord(int value) {
            int doubled() {
                return value * 2;
            }

            static LocalRecord zero() {
                return new LocalRecord(0);
            }
        }
        enum LocalEnum {
            ON, OFF;

            boolean isOn() {
                return this == ON;
            }

            static final LocalEnum DEFAULT = OFF;
        }
        interface LocalInterface {
            void call();

            int VERSION = 1;
        }
    }

    {
        class InInitializer {
            void m() {
            }

            int f;
        }
    }

    Supplier<Object> supplier = () -> {
        class InLambda {
            void m() {
            }

            int f;
        }
        return new InLambda();
    };
}
