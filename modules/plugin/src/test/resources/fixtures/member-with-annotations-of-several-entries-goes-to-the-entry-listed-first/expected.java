package fixtures;

class CartTest {
    @BeforeEach
    void setUp() {
    }

    @Disabled
    @Test
    void addsItem() {
    }

    @Test
    void startsEmpty() {
    }

    private Cart cart() {
        return new Cart();
    }

    @Disabled
    void flaky() {
    }
}
