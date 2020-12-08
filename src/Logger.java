import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private final int logLevel;
    private final FileWriter logFile;
    private static Logger loggerInstance;

    private Logger(int logLevel, FileWriter logFile)
    {
        this.logLevel = logLevel;
        this.logFile = logFile;
    }

    public Logger getInstance(int logLevel, FileWriter logFile)
    {
        if(loggerInstance == null) {
            loggerInstance = new Logger(logLevel, logFile);
        }

        return loggerInstance;
    }

    private void writeToFile(String message)
    {
        //Helper function to prevent code duplication
        try{
            logFile.write(message);
        } catch(IOException exp)
        {
            System.out.println("Logger has encountered an error while writing to file!\n");
        }
    }

    /**
     *
     * @param code error code coming with the message used in comparison with the logLevel
     * @param message the error message sent to be logged
     */

    public void log(LogCode code, String message)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String timeStamp = "[ "+formatter.format(date)+" ]";
        String errString = timeStamp + " " + code.name() + ": " + message + "\n";
        if(logLevel == 0){
            if(code == LogCode.FATAL){
                this.writeToFile(errString);
            }
        } else if(logLevel == 1) {
            if (code == LogCode.FATAL || code == LogCode.ERR){
                this.writeToFile(errString);
            }
        } else if(logLevel == 2) {
            if (code == LogCode.FATAL || code == LogCode.ERR || code == LogCode.WARN){
                this.writeToFile(errString);
            }
        } else if(logLevel == 3) {
            if (code == LogCode.FATAL || code == LogCode.ERR || code == LogCode.WARN || code == LogCode.INFO){
                this.writeToFile(errString);
            }
        }
        else if(logLevel == 4) {
            this.writeToFile(errString);
        }
    }
}
