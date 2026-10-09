package fixtures;

class Checkout {
    @Inject
    void setClock(Clock clock) {
        this.clock = clock;
    }

    void pay() {
    }

    @Inject
    private PaymentGateway gateway;

    @Inject
    Checkout(Cart cart) {
        this.cart = cart;
    }

    private final Retry retry = Retry.using(gateway);

    private Cart cart;

    private Clock clock;
}
