import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.*;

/**
 * Class performs the functionalities of downloading a web page,
 * parsing it to replace the links within it according to
 * {@link Config#rootDir} path and saving the modified content
 * in specific file from our file hierarchy.
 *
 * @author Andrei Brinzea
 * @author Ciobanu Cosmin-Marian
 */

public class PageCrawler extends Thread{
    /**
     * Members of class <b>PageCrawler</b>
     * <i>maxDepth :</i> maximum depth could have in
     * downloaded process
     * <i>maxSize :</i> maximum size in bytes could have
     * downloaded content page
     * <i>currURL :</i> store information about the current
     * URL. Is is a {@link URLString} object.
     */

    private int maxDepth;
    private long maxSize;
    private URLString currUrl;

    /**
     * <b>PageCrawler</b> class constructor
     * @param depth maximum depth could have in download process
     * @param size maximum size in bytes could have downloaded page
     */
    public PageCrawler(int depth, long size) {
        this.maxDepth = depth;
        this.maxSize = size;
    }

    /**
     * Function used to start thread, which overrides
     * <i>start</i> method from <b>Exception</b> Java class
     */
    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * Function used to specify code what want to be executed
     * when a PageCrawler instance call {@link PageCrawler#start()}
     * method.
     * This function set a Http connection with server.
     * Before download, the page depth level is check
     * if it is lower than {@link #maxDepth} and if it's true, than
     * the process for download the page can continue.
     * Also, it is checked if the size of the download content page
     * is less than or equal to {@link #maxSize}.
     */
    @Override
    public void run() {
        HttpURLConnection httpConn; //Wrapper for connection
        int responseCode; //Response code for check connection status
        long contentLength; //Page content length
        String pageContent; //Page content
        String newPageContent; //New Page content after parse
        int bytesRead; //Number of bytes read
        int totalBytesRead; //Total number of bytes read
        byte[] buffer = new byte[4096]; //Buffer in which store downloaded bytes
        ByteArrayOutputStream bytesBuffer; //Buffer in which store all downloaded bytes
        InputStream inputStream; //Used for working with Http input stream
        String urlString = this.currUrl.getUrlString().toString(); //Url string used for log

        try {
            Logger logger = Logger.getInstance();

            try {
                while ((currUrl = StackManager.getInstance().PopURL()) != null) {
                    try {
                        Thread.sleep(this.getCrawlDelay()); //Apply crawl delay

                        if (maxDepth < currUrl.getDepth()) { //Check maxDepth
                            throw new UnknownException("Maximum depth exceeded to page:" + urlString, LogCode.WARN);
                        }

                        httpConn = (HttpURLConnection) this.currUrl.getUrlString().openConnection();
                        responseCode = httpConn.getResponseCode();
                        if (responseCode != HttpURLConnection.HTTP_OK) {
                            throw new ConnectionException("Invalid connection to page:" + urlString, LogCode.ERR);
                        }

                        /*
                         * First, it is checked if value of the content-length header
                         * field from Http method getContentLengthLong().
                         * But in some situations, that field is not set and return -1.
                         * For case when return -1, will be checked when download the
                         * page if the size is exceeded.
                         */

                        contentLength = httpConn.getContentLengthLong();
                        if (contentLength > 0 && contentLength > this.maxSize) {
                            throw new UnknownException("Size exceeded for download page:" + urlString, LogCode.WARN);
                        }
                        inputStream = httpConn.getInputStream();
                        bytesBuffer = new ByteArrayOutputStream();

                        logger.log(LogCode.INFO,"Downloading " +  urlString);

                        bytesRead = 0;
                        totalBytesRead = 0;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            bytesBuffer.write(buffer, 0, bytesRead);
                            totalBytesRead += bytesRead;

                            if (totalBytesRead > this.maxSize) { //Check if max size is exceeded
                                inputStream.close();
                                bytesBuffer.close();
                                throw new UnknownException("Size exceeded for download page:" + urlString,
                                        LogCode.WARN);
                            }
                        }

                        inputStream.close();
                        pageContent = bytesBuffer.toString(StandardCharsets.UTF_8);
                        bytesBuffer.close();

                        logger.log(LogCode.INFO,"Page " +  urlString + " has been downloaded!");

                        newPageContent = this.parse(pageContent); //Achieve modified Page
                        logger.log(LogCode.INFO,"Page " +  urlString + " has been parsed!");

                        if (!makeFS(newPageContent)) {
                            throw new FileException("Error saving page:" + urlString, LogCode.ERR);
                        }
                        logger.log(LogCode.INFO,"Page " +  urlString + " has been saved to file!");

                    } catch (InterruptedException | IOException e) {
                        logger.log(LogCode.ERR, e.getMessage());
                    } catch (ConnectionException | FileException | UnknownException e) {
                        e.printException();
                    }
                }
            } catch (EmptyStackException e) {
                logger.log(LogCode.INFO,"The stack with links for download is empty!");
            }
        } catch (IOException e) {
            System.out.println("[FATAL]: Could not get instance of logger");
        }

    }

    /**
     * Function tha returns the crawl delay that should be
     * applied to crawl during the download process.
     * If is wanted to apply crawl delay from <i>Robots.txt</i>
     * file, than it is checked if there is a value for crawl
     * delay and returned this valued if exists. If doesn't
     * exist, crawl delay return the default value.
     *
     * @return crawl delay from <i>Robots.txt</i> if it exists
     * and {@link Config#ignoreRobots} is not set, otherwise
     * return default value from {@link Config#delay}
     */
    private int getCrawlDelay(){

        //Default crawl delay
        int defaultDelay = Config.getInstance().delay;

        // Check if ignoreRobots value is not set
        if (Config.getInstance().ignoreRobots == 0) {
            int robotsDelay = StackManager.getInstance().getDelayForRobot(this.currUrl);

            //Check if exists crawl delay value in Robots.txt
            if (robotsDelay > 0) {
                try {
                    Logger.getInstance().log(LogCode.INFO, "Applied crawl delay with value :"
                                             + robotsDelay + " from robots.txt file!");
                } catch (IOException e) {
                    System.out.println("[FATAL]: Could not get instance of logger");
                }

                return robotsDelay;
            }
        }

        try {
            Logger.getInstance().log(LogCode.INFO, "Applied crawl delay with value :"
                                     + defaultDelay + " from configuration file!");
        } catch (IOException e) {
            System.out.println("[FATAL]: Could not get instance of logger");
        }

        return defaultDelay;
    }

    private String parse(String content) {
        return "";
    }

    /**
     * Class that save the content of the page, after parsing it,
     * in the specific file.
     * Depending on the URL of the page, the position of the
     * newly created file in the local files hierarchy is calculated
     * and the respective path is created in case it does not exist.
     *
     * @param finalContent the final modified content of the page
     * @return true if the page has been save with success, otherwise
     * return false
     */
    private boolean makeFS(String finalContent) {

        //Extract path where to save page from URL
        String path = this.currUrl.getUrlString().getFile();
        int position; //used for check page extension
        String[] dirs = path.split("/"); //Store all dirs from path

        //Store dirs from String[] dirs in ArrayList
        ArrayList<String> arrayFiles = new ArrayList<String>(Arrays.asList(dirs));
        File forCreate; //Used to create dirs for path, if don't exist
        String rootPath = Config.getInstance().rootDir; //Used to create path from rootDir
        FileOutputStream outputStream; //Used for write content page to file
        boolean check_createDir;
        int i;

        //Extract position for extension for check after if exists or not
        position = arrayFiles.get(arrayFiles.size() - 1).lastIndexOf('.');

        //Check if extension for page does not exist
        if (position < 0) {

            //Add a default page "index.html" where save content of page
            arrayFiles.add("index.html");

        }

        try {
            for (i = 0; i < arrayFiles.size() - 1; i++) {
                rootPath += '/' + arrayFiles.get(i);
                forCreate = new File(rootPath);

                if (!forCreate.exists()) {

                    //create dir if doesn't exist
                    if (!forCreate.mkdirs()) {
                        throw new FileException("Couldn't create dir:" + rootPath + " for download page:" +
                                currUrl.getUrlString().toString(), LogCode.ERR);
                    }
                }
            }

            rootPath += '/' + arrayFiles.get(arrayFiles.size() - 1);
            Logger.getInstance().log(LogCode.INFO, "Path for " + currUrl.getUrlString().toString() +
                                      " is valid and content can be saved there!");

            outputStream = new FileOutputStream(rootPath);
            outputStream.write(finalContent.getBytes());
            outputStream.close();

            Logger.getInstance().log(LogCode.INFO, "Content for " + currUrl.getUrlString().toString() +
                                      " has been saved!");

        } catch (FileException | IOException e) {

            if (e instanceof IOException) {
                System.out.println("[FATAL]: Could not get instance of logger");
            }

            return false;
        }

        return true;
    }

}
