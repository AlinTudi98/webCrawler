/**
 * Abstract Class that represents the base class used in exception
 * mechanism.
 * Extends the functionality of the <i>Exception</i> class from
 * the Java Api.
 *
 * @author Andrei Brinzea
 */
public abstract class CrawlException extends Exception {
    /** Error code that appears */
    protected ErrorCode errCode;

    /**
     * CrawlException class constructor
     * @param errMessage The error message is specific to the errors
     *                   that occurred.
     * @param errCode The error code is specific to the errors that
     *                occurred.
     */
    public CrawlException(String errMessage, ErrorCode errCode) {
        this.errCode = errCode;

        // TODO: Write exception message in log file
        // TODO: Java Exception class constructor need to be called
    }

    /**
     * Abstract method for displaying specific error messages.
     * Need to be implemented in derived classes.
     */
    abstract void printException();
}