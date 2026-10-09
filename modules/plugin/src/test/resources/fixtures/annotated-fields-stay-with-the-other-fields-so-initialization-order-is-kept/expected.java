package fixtures;

class Checkout {
    @Inject
    private PaymentGateway gateway;

    private final Retry retry = Retry.using(gateway);

    private Cart cart;

    private Clock clock;

    @Inject
    void setClock(Clock clock) {
        this.clock = clock;
    }

    @Inject
    Checkout(Cart cart) {
        this.cart = cart;
    }

    void pay() {
    }
}
