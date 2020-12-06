/**
 * Derived class from {@link CrawlException} base exception class.
 * It is used for exceptions due to unknown errors.
 *
 * @author Andrei Brinzea
 * @see CrawlException
 */
public class UnknownException extends CrawlException{
    /**
     * UnknownException class constructor which call base class
     * constructor {@link CrawlException#CrawlException(String, errorCode)}
     * @param errMessage The error message is specific to the
     *                   unknown errors that occurred.
     * @param errCode The error code is specific to the unknown
     *                errors that occurred.
     */
    public UnknownException(String errMessage, errorCode errCode) {
        super(errMessage, errCode); // Call base class constructor
    }

    /**
     * Method for displaying specific unknown error message
     * and error code that occurred.
     * Override the abstract function
     * {@link CrawlException#printException()} from base class.
     */
    @Override
    void printException() {
        System.err.println("UNKNOWN_ERROR: " + this.getMessage()
                + " with code " + this.errCode);
    }
}
