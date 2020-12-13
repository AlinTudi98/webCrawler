import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class performs the functionalities of downloading a web page,
 * parsing it to replace the links within it according to
 * {@link Config#rootDir} path and saving the modified content
 * in specific file from our file hierarchy.
 *
 * @author Andrei Brinzea
 * @author Ciobanu Cosmin-Marian
 */

public class PageCrawler extends Thread {
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
    private static int threadsWaiting = 0;
    private final Object lock = new Object();

    /**
     * <b>PageCrawler</b> class constructor
     *
     * @param depth maximum depth could have in download process
     * @param size  maximum size in bytes could have downloaded page
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
        String filepath; //Store file path
        String pageName; //Store pageName
        String pageExtension; //Used for store page extension
        int bytesRead; //Number of bytes read
        int totalBytesRead; //Total number of bytes read
        int i;
        int check_pageExtension; //Used for check page extension
        byte[] buffer = new byte[4096]; //Buffer in which store downloaded bytes
        ByteArrayOutputStream bytesBuffer; //Buffer in which store all downloaded bytes
        InputStream inputStream; //Used for working with Http input stream
        String urlString = this.currUrl.getUrlString().toString(); //Url string used for log

        try {
            Logger logger = Logger.getInstance();

            try {
                while (true) {
                    synchronized (lock) {
                        threadsWaiting++;
                    }

                    while ((currUrl = StackManager.getInstance().PopURL()) == null) {
                        if (threadsWaiting == Config.getInstance().numThreads)
                            return;
                    }

                    synchronized (lock) {
                        threadsWaiting--;
                    }

                    try {
                        Thread.sleep(this.getCrawlDelay()); //Apply crawl delay

                        if (maxDepth < currUrl.getDepth()) { //Check maxDepth
                            throw new UnknownException("Maximum depth exceeded to page:" + urlString, LogCode.WARN);
                        }

                        filepath = currUrl.getUrlString().getFile(); //Get file path

                        pageName = filepath.split("/")[filepath.split("/").length - 1]; //Extract page name
                        pageExtension = pageName.substring(pageName.lastIndexOf(".") + 1); //Extract page extension

                        check_pageExtension = 0; //Unset check page extension
                        for ( i = 0; i < Config.getInstance().dTypes.length; i++ ) {

                            //Check if page extension is in allowed type list
                            if (pageExtension.equals(Config.getInstance().dTypes[i])) {
                                check_pageExtension = 1; //Set check page extension
                                break;
                            }
                        }

                        /*
                         * Check if the page does not have the .html/.htm
                         * extension or if it has an extension and it's not
                         * in the list of valid extensions, then the page
                         * will not be downloaded
                         */
                        if (check_pageExtension == 0 && !pageExtension.equals("html") &&
                                !pageExtension.equals("htm") && !pageExtension.equals(pageName)) {

                            //Thrown exception because page extension is not valid
                            throw new UnknownException("Page:" + urlString + " does not have a valid extension" +
                                    " to be downloaded!", LogCode.WARN);
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

                        logger.log(LogCode.INFO, "Downloading " + urlString);

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

                        logger.log(LogCode.INFO, "Page " + urlString + " has been downloaded!");

                        newPageContent = this.parse(pageContent); //Achieve modified Page
                        logger.log(LogCode.INFO, "Page " + urlString + " has been parsed!");

                        if (!makeFS(newPageContent)) {
                            throw new FileException("Error saving page:" + urlString, LogCode.ERR);
                        }
                        logger.log(LogCode.INFO, "Page " + urlString + " has been saved to file!");

                    } catch (InterruptedException | IOException e) {
                        logger.log(LogCode.ERR, e.getMessage());
                    } catch (ConnectionException | FileException | UnknownException e) {
                        e.printException();
                    }
                }
            } catch (EmptyStackException e) {
                logger.log(LogCode.INFO, "The stack with links for download is empty!");
            }
        } catch (IOException e) {
            System.out.println("[FATAL]: Could not get instance of logger");
        }

    }

    /**
     * Function that returns the crawl delay that should be
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
    private int getCrawlDelay() {

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

    /**
     * This method deals with all links that have a full format <b>(eg. https?://example.com/*)</b>
     *
     * @param text is the contents of the file to be parsed
     * @return returns the received file as a parameter with the new paths in the file system
     * @throws MalformedURLException if the is any invalid URL
     */

    private String changeURLs(String text) {
        /**
         * newContain is the copy of our file contain
         * urlPattern is the pattern for any URL which is complete
         * pattern and matcher are used for REGEX validation
         */
        String newContain = text;
        String urlPattern = "\\b(https?://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern pattern = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        String priorityStr = "";
        try {
            try {
                while (matcher.find()) { // while there are sites unchanged

                    String baseStr = matcher.group();
                    StringTokenizer token = new StringTokenizer(baseStr, "#?"); //we want to use the part of the link until "?"

                    if (token.hasMoreElements()) { // if there is any "?" we take de link until it
                        priorityStr = token.nextToken();
                    } else { // else we take the whole link
                        priorityStr = baseStr;
                    }

                    URL currentUrl = new URL(priorityStr);
                    String portionToChange = currentUrl.getProtocol() + "://" + currentUrl.getHost() + "/"; //we take the part of the link which needs to be changed with path to root directory

                    if (portionToChange != currUrl.getUrlString().toString()) { //if the new link is not the current one of our page

                        StackManager.getInstance().addRobot(new URLString(new URL(portionToChange), currUrl.getDepth())); //we add a Robot instance if our link has robots.txt file
                    }
                    // current's match path is changed
                    String replacedStr = priorityStr.replace(portionToChange, Config.getInstance().rootDir);

                    // the link is added to stack
                    StackManager.getInstance().PushURL(new URLString(new URL(priorityStr), currUrl.getDepth()));
                    newContain = newContain.replace(baseStr, replacedStr);
                }
            } catch (MalformedURLException e) {
                Logger.getInstance().log(LogCode.WARN, "[WARN] PageCrawler: MalformedURLException thrown for line: \"" + priorityStr + "\". Line has been ignored.");
            }
        } catch (IOException e) {
            System.out.println("[FATAL]: Could not get instance of logger");
        }
        return newContain;
    }

    /**
     * The main method for parsing files.
     * Here all the files required by the threads are parsed and
     * their paths are replaced to mold to the local file system
     * created by the utility.
     * It must perform the respective operations unless the received
     * file has the extension <i>".html" / ".htm"</i> or has <i>no extension<i/> at all.
     * In the last case initial file is returned unchanged.
     *
     * @param content is the original file which needs to be parsed
     * @return the original file with all links changed
     * @throws MalformedURLException if the is any invalid URL
     */
    private String parse(String content) {
        // First of all verify if this file should be parsed or not

        String filePath = currUrl.getUrlString().getFile(); // get file path
        String fileName = filePath.split("/")[filePath.split("/").length - 1]; //extract last element from path
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1); //get the extension of this file

        if (!fileExtension.equals("html") && !fileExtension.equals(fileName) && !fileExtension.equals("htm")) { // if it has ".html" extension or has no extension
            return content; // return the content without any change
        }

        /**
         * newContain: string used as copy of the file content
         * baseUrl: extracted base URL from that one which is used by this instance of PageCrawler
         * partialURLs: REGEX for relative URLs
         * pattern and matcher are used for REGEX verification
         */
        String newContain = "";
        try {
            String toAddInStack = "";
            try {

                newContain = changeURLs(content);
                String baseUrl = currUrl.getUrlString().getProtocol() + "://" + currUrl.getUrlString().getHost() + "/";
                String partialURLs = "((href)|(src)) ?= ?\"\\/[-A-Za-z0-9+&@#\\/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#\\/%=~_()|]";
                Pattern pattern = Pattern.compile(partialURLs);
                Matcher matcher = pattern.matcher(newContain);

                while (matcher.find()) {
                    String baseStr = matcher.group();

                    StringTokenizer token = new StringTokenizer(baseStr, "#?");//we want to use the part of the link until "?"
                    // Url made with relative path
                    String priorityStr = "";

                    if (token.hasMoreElements()) { // if there is any "?" we take de link until it
                        priorityStr = token.nextToken();
                    } else { // else we take the whole link
                        priorityStr = baseStr;
                    }
                    //is used only that part which involve a link
                    String changedStr = priorityStr.replaceAll("((href)|(src)) ?= ?", "").trim();
                    //is changed the old path with the new one which has as reference the file system
                    newContain = newContain.replace(changedStr, Config.getInstance().rootDir + changedStr.substring(2));

                    changedStr = priorityStr.replaceAll("((href)|(src)) ?= ?\"\\/", "").trim();
                    toAddInStack = baseUrl + changedStr;
                    //the new link is added in URLs stack from StackManager
                    StackManager.getInstance().PushURL(new URLString(new URL(toAddInStack), currUrl.getDepth() + 1));

                }
            } catch (MalformedURLException e) {
                Logger.getInstance().log(LogCode.WARN, "PageCrawler: MalformedURLException thrown for line: \"" + toAddInStack + "\". Line has been ignored.");
            }
        } catch (IOException e) {
            System.out.println("[FATAL]: Could not get instance of logger");
        }

        return newContain;
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
            for ( i = 0; i < arrayFiles.size() - 1; i++ ) {
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
