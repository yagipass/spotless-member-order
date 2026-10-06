package fixtures;

public class Documented {
    /**
     * Returns the name.
     *
     * @return the name
     */
    @Deprecated(since = "2.0")
    @SuppressWarnings("unused")
    public String name() {
        // comment inside the method body stays inside
        return name; // trailing comment inside the body
    }

    // leading line comment about the field
    /** Javadoc of the field. */
    @Deprecated
    /* block comment between annotation and declaration */
    private String name; // trailing comment of the field

    /*
     * Multi-line block comment
     * about the constructor.
     */
    public Documented() {
    } /* trailing block comment of the constructor */

    // first line of a leading comment group
    // second line of a leading comment group
    static final int VERSION = 2;
}
