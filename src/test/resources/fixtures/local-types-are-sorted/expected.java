package fixtures;

import java.util.function.Supplier;

class Locals {
    {
        class InInitializer {
            int f;

            void m() {
            }
        }
    }

    Supplier<Object> supplier = () -> {
        class InLambda {
            int f;

            void m() {
            }
        }
        return new InLambda();
    };

    void method() {
        class LocalClass {
            int count;

            void run() {
            }
        }
        record LocalRecord(int value) {
            static LocalRecord zero() {
                return new LocalRecord(0);
            }

            int doubled() {
                return value * 2;
            }
        }
        enum LocalEnum {
            ON, OFF;

            static final LocalEnum DEFAULT = OFF;

            boolean isOn() {
                return this == ON;
            }
        }
        interface LocalInterface {
            int VERSION = 1;

            void call();
        }
    }
}
