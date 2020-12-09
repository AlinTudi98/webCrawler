import java.io.FileWriter;
import java.io.IOException;

/**
 * Abstract Class that represents the base class used in exception
 * mechanism.
 * Extends the functionality of the <i>Exception</i> class from
 * the Java Api.
 *
 * @author Andrei Brinzea
 */
public abstract class CrawlException extends Exception {
    /** Log code that appears */
    protected LogCode logCode;

    /**
     * CrawlException class constructor
     * @param errMessage The error message is specific to the errors
     *                   that occurred.
     * @param _logCode The log code is specific to the errors that
     *                 occurred.
     */
    public CrawlException(String errMessage, LogCode _logCode) {
        super(errMessage);
        this.logCode = _logCode;

        try {

            //Log message error in file
            Logger.getInstance().log(this.logCode, errMessage);
        } catch (IOException e) {

            //Print error in console
            System.out.println("Error log file: Can't be open for log errors!");
        }
    }

    /**
     * Abstract method for displaying specific error messages.
     * Need to be implemented in derived classes.
     */
    abstract void printException();
}