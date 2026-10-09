package fixtures;

class CartTest {
    @Disabled
    void flaky() {
    }

    @Disabled
    @Test
    void addsItem() {
    }

    private Cart cart() {
        return new Cart();
    }

    @Test
    void startsEmpty() {
    }

    @BeforeEach
    void setUp() {
    }
}
