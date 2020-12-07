
public class UnknownException extends CrawlException{
    public UnknownException(String errMessage, errorCode errCode) {
        super(errMessage, errCode);
    }

    @Override
    void printException() {
        System.err.println("UNKNOWN_ERROR: " + this.getMessage()
                            + " with code " + this.errCode);
    }
}
