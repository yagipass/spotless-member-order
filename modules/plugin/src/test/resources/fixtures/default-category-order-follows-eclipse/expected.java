package fixtures;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    public static class Order {
    }

    static {
        System.setProperty("orders.loaded", "true");
    }

    private static final int LIMIT = 10;

    public static OrderService create() {
        return new OrderService();
    }

    private final List<String> orders = new ArrayList<>();

    {
        orders.add("first");
    }

    public OrderService() {
    }

    public List<String> orders() {
        return new ArrayList<>(orders);
    }

    void clear() {
        orders.clear();
    }
}
