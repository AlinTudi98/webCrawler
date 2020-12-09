/**
 * Derived class from {@link CrawlException} base exception class.
 * It is used for exceptions due to working with files.
 *
 * @author Andrei Brinzea
 * @see CrawlException
 */
public class FileException extends CrawlException{
    /**
     * FileException class constructor which call base class
     * constructor {@link CrawlException#CrawlException(String, LogCode)}
     * @param errMessage The error message is specific to the file
     *                   errors that occurred.
     * @param _logCode The log code is specific to the file errors
     *                 that occurred.
     */
    public FileException(String errMessage, LogCode _logCode) {
        super(errMessage, _logCode); // Call base class constructor
    }

    /**
     * Method for displaying specific file error message and log
     * code that occurred.
     * Override the abstract function
     * {@link CrawlException#printException()} from base class.
     */
    @Override
    void printException() {
        System.err.println("FILE_ERROR: " + this.getMessage()
                + " with code " + this.logCode);
    }
}
