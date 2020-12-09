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
     * constructor {@link CrawlException#CrawlException(String, LogCode)}
     * @param errMessage The error message is specific to the
     *                   unknown errors that occurred.
     * @param _logCode The log code is specific to the unknown
     *                 errors that occurred.
     */
    public UnknownException(String errMessage, LogCode _logCode) {
        super(errMessage, _logCode); // Call base class constructor
    }

    /**
     * Method for displaying specific unknown error message
     * and <i>FATAL</i> log code that occurred.
     * Override the abstract function
     * {@link CrawlException#printException()} from base class.
     */
    @Override
    void printException() {

        /*
         * Check if log code is FATAL, only in that case,
         * message and log code will be printed in the console.
         */
        if (this.logCode == LogCode.FATAL) {
            System.err.println("UNKNOWN_ERROR: " + this.getMessage()
                                + " with code " + this.logCode);
        }
    }
}
