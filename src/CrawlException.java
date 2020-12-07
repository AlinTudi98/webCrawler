enum errorCode {
    WARN,
    ERR,
    FATAL
}

public abstract class CrawlException extends Exception {
    protected errorCode errCode;

    public CrawlException(String errMessage, errorCode errCode) {
        this.errCode = errCode;

        //TO DO: Write exception message in log file
    }

    abstract void printException();
}