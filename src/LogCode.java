/**
 * Enum saves possible types of log codes.
 * It is used to be able to determine the flow
 * of program further depending on the type of code that occurred.
 * These types of logs are used in the exception mechanism
 * and a field of this enum type is {@link CrawlException#logCode}.
 * It is also used by the {@link Logger#log(LogCode, String)}
 * function from {@link Logger} class.
 *
 * @author Andrei Brinzea
 */
public enum LogCode {
    /**
     * <i>Info</i> type code
     */
    INFO,

    /**
     * <i>Warn</i> type code
     */
    WARN,

    /**
     * <i>Err</i> type code
     */
    ERR,

    /**
     * <i>Fatal</i> type code
     */
    FATAL
}
