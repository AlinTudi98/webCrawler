import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.*;

public class PageCrawler extends Thread{

    private int maxDepth;
    private long maxSize;
    private URLString currUrl;

    public PageCrawler(int depth, long size) {
        this.maxDepth = depth;
        this.maxSize = size;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

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

    private boolean makeFS(String finalContent) {
        return true;
    }

}
