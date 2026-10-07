/** Module documentation. */
module fixtures.app {
    requires transitive java.logging;
    exports fixtures.api;
    uses java.util.spi.ToolProvider;
    provides java.util.spi.ToolProvider with fixtures.Tool;
}
