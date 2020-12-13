import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.*;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *  This class is tasked with extracting the valid URLs from the input,
 *  be it either a text file, or an input string from the command line.
 *  URLParser is the first class that will run during the crawl process
 *  and its behavior is of critical importance, since the URLParser
 *  object will create the download stack of the StackManager class
 *  that manages what URLs will be downloaded.
 *
 * @author Alin Tudose
 */
public class URLParser {


    /**
     * This function is used to verify if a string contains a valid URL.
     *
     * @param url The string we want to verify.
     * @return True if the string contains a valid URL, and False
     *         otherwise.
     */
    boolean validateURL(String url){

        String urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern urlPattern = Pattern.compile(urlRegex);
        Matcher urlMatcher = urlPattern.matcher(url);

        if(urlMatcher.find()){
            String testUrl = urlMatcher.group(0);
            if(url.equalsIgnoreCase(testUrl))
                return true;
            return false;
        }
        return false;
    }

    /**
     * This function is used to parse the contents of a text file,
     * extracting it line by line and verifying if the line is, in fact
     * a valid URL. All valid URLs found are pushed into the download
     * stack of the StackManager class.
     *
     * @param urlFile The file we read from.
     */
    public void parse(File urlFile) {
        try {
            Logger logger = Logger.getInstance();
            try {
                logger.log(LogCode.INFO,"URLParser: Successfully started URLParser class for file " + urlFile.getPath());
                StackManager stackManager = StackManager.getInstance();
                Scanner reader = new Scanner(urlFile);

                while (reader.hasNextLine()) {
                    String line = reader.nextLine();

                    if (validateURL(line)) {
                        try {
                            URL url = new URL(line);
                            URLString urlString = new URLString(url, 0);
                            stackManager.addRobot(urlString);
                            stackManager.PushURL(urlString);
                            logger.log(LogCode.INFO, "URLParser: Added URL: \"" + line + "\" to download stack.");

                        } catch (MalformedURLException e) {
                            logger.log(LogCode.WARN, "URLParser: MalformedURLException thrown for line: \"" + line + "\". Line has been ignored.");
                        }
                    } else {
                        logger.log(LogCode.WARN, "URLParser: Ignored line: \"" + line + "\". Not a valid URL.");
                    }

                }
                reader.close();

            }
            catch (FileNotFoundException ee) {
                logger.log(LogCode.FATAL,"URLParser: Could not open input file.");
            }
        }
        catch(IOException e)
        {
            System.out.println("[FATAL]: Could not get instance of logger");
        }

    }

    /**
     * This function is used to parse the contents of a string,
     * to verify if it contains a valid URL. If it does, the URL is
     * pushed into the download stack of the StackManager class.
     * If we want to pass multiple URLs in the String parameter, the
     * URLs should be separated by commas.
     *
     * @param urlString The String we extract the URLs from.
     */
    public void parse(String urlString){
        try {
            Logger logger = Logger.getInstance();
            logger.log(LogCode.INFO,"URLParser: Successfully started URLParser class for string " + urlString);
            StackManager stackManager = StackManager.getInstance();

            for (String line : urlString.split(",")) {
                if (validateURL(line)) {
                    try {
                        URL url = new URL(line);
                        URLString urlStr = new URLString(url, 0);
                        stackManager.addRobot(urlStr);
                        stackManager.PushURL(urlStr);
                        logger.log(LogCode.INFO, "URLParser: Added URL: \"" + line + "\" to download stack.");
                    } catch (MalformedURLException e) {
                        logger.log(LogCode.WARN, "URLParser: MalformedURLException thrown for string: \"" + line + "\". String has been ignored.");
                    }
                } else {
                    logger.log(LogCode.WARN, "URLParser: Ignored string: \"" + line + "\". Not a valid URL.");
                }
            }
        }
        catch(IOException e) {
            System.out.println("[FATAL]: Could not get instance of logger");
        }
    }
}
