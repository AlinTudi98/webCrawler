
public class FileException extends CrawlException{
    public FileException(String errMessage, errorCode errCode) {
        super(errMessage, errCode);
    }

    @Override
    void printException() {
        System.err.println("FILE_ERROR: " + this.getMessage()
                            + " with code " + this.errCode);
    }
}
