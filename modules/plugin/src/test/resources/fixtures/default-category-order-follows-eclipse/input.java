package fixtures;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    public List<String> orders() {
        return new ArrayList<>(orders);
    }

    private final List<String> orders = new ArrayList<>();

    public OrderService() {
    }

    static {
        System.setProperty("orders.loaded", "true");
    }

    public static OrderService create() {
        return new OrderService();
    }

    private static final int LIMIT = 10;

    {
        orders.add("first");
    }

    public static class Order {
    }

    void clear() {
        orders.clear();
    }
}
