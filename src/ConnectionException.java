
public class ConnectionException extends CrawlException {
    public ConnectionException(String errMessage, errorCode errCode) {
        super(errMessage, errCode);
    }

    @Override
    void printException() {
        System.err.println("CONNECTION_ERROR: " + this.getMessage()
                            + " with code " + this.errCode);
    }
}
