import java.io.File;


/**
 * This class is meant to log all given messages from different
 * sources and assign a timestamp to message meant to be logged.
 *
 * @author Ghita Alexandru-Andrei
 *
 */

public class Logger {

    /**
     * Members of  class Logger
     * logLevel: represents a threshold that the Logger uses for
     *           choosing what to and what not to log. 0 - logs
     *           only fatal messages, 1 - logs all errors, 2 -
     *           logs warnings in additions, 3 - logs information,
     *           4 - logs information from crawler running process.
     * logFile: the file to which the information is written
     * loggerInstance: Logger is a singleton class
     */

    private int logLevel;
    private File logFile;
    private static Logger loggerInstance;

    private Logger(int logLevel, File logFile)
    {
        this.logLevel = logLevel;
        this.logFile = logFile;
    }

    public Logger getInstance(int logLevel, File logFile)
    {
        if(loggerInstance == null)
            loggerInstance = new Logger(logLevel, logFile);

        return loggerInstance;
    }

}
