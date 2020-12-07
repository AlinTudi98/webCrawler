/**
 * Enum saves possible types of errors.
 * It is used to be able to determine the flow
 * of program further depending on the type of error.
 * These types of errors are used in the exception mechanism
 * and a field of this enum type is {@link CrawlException#errCode}.
 *
 * @author Andrei Brinzea
 */
public enum ErrorCode {
    /**
     * <i>Warn</i> type error
     */
    WARN,

    /**
     * <i>Err</i> type error
     */
    ERR,

    /**
     * <i>Fatal</i> type error
     */
    FATAL
}
