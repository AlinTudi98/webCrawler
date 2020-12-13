import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 *   This class deals with the centralization of downloaded links and the
 *   synchronization of threads when they interact with them.
 *   The contents of the robots.txt files are also saved here and saved in a list.
 *
 *   @author Ciobanu Cosmin-Marian
 */

public class StackManager {

    /**
     *   Members of the class
     *   stackmanagerInstance: instance of StackManager class used for Singleton implementation
     *   urlStack: a stack with all the saved links which follows to be used by the threads
     *   robotsList: a list with all the Robot instances found at a moment of time
     *   lock: is an Object used only to avoid "Race concurrence" case between threads
     *
     */

    private static StackManager stackmanagerInstance = null;
    private Stack<URLString> urlStack;
    private ArrayList<Robot> robotsList;
    private final Object lock = new Object();

    //private constructor requested by Singleton pattern
    private StackManager(){

        urlStack = new Stack<URLString>();
        robotsList = new ArrayList<Robot>();
    }

    /**
     *
     * @return the only object made from Singleton instance or creates it if is null
     */
    public static StackManager getInstance(){

        if(stackmanagerInstance == null){
            stackmanagerInstance = new StackManager();
        }
        return stackmanagerInstance;
    }

    /**
     * Add a URLString to urlStack, but first verify if this
     * link is not in any disallow list of a Robot.
     * Also it should not add the same link twice so,
     * in addition to the operations specified above, also check urlStack.
     * @param url is the new link which needs to be added in stack
     */
    public void PushURL(URLString url){

        synchronized (lock){

            boolean availability=true;
            if (!robotsList.isEmpty()) {
                for ( Robot iterator : robotsList ) { //verify if the new link is not in disallowed list of a Robot
                    if (!iterator.verifyURL(url)) {
                        availability = false;
                        break;
                    }
                }
            }
            //verify if this url is not already in Stack
            if(!urlStack.isEmpty()) {
                for ( URLString iterator : urlStack ) {
                    if (url.getUrlString().equals(iterator.getUrlString())) {
                        availability = false;
                        break;
                    }
                }
            }
            try {
                if (availability){ // if url link is a valid one it will be added in stack
                    Logger.getInstance().log(LogCode.INFO, "StackManager: Added URL: \"" + url + "\" to download stack.");
                    urlStack.push(url);
                }
            }catch(IOException e)
            {
                System.out.println("[FATAL]: Could not get instance of logger");
            }

        }
    }

    /**
     * The function which need to return last element
     * added in urlStack.
     * @return first URLString from stack or throw exception if the stack is empty
     * @throws EmptyStackException if the stack has no element
     */

    public URLString PopURL(){

        URLString firstElement;

        if (urlStack.empty()){
           throw new EmptyStackException();
        }

        synchronized (lock){
            firstElement = urlStack.pop();
            return firstElement;
        }
    }

    /**
     *  Will add a new Robot for one link if it is not already in list.
     * @param url is the new link which might have a robots.txt file and is not already added
     * @throws MalformedURLException if the is any invalid URL
     */
    public void addRobot( URLString url){
        try {

            try {

                Robot newRobotToAdd = new Robot(url.getUrlString());

                synchronized (lock) {
                    for ( Robot iterator : robotsList ) {
                        if (iterator.getbaseUrlOfRobot().equals(url.getUrlString())) {
                            return;
                        }
                    }
                    robotsList.add(newRobotToAdd);
                    Logger.getInstance().log(LogCode.INFO, "StackManager: Added URL: \"" + url.getUrlString().toString() + "\" to Robots list.");
                }

            } catch (MalformedURLException ignored) {
                Logger.getInstance().log(LogCode.WARN, "StackManager: MalformedURLException thrown for line: \"" + url.getUrlString().toString() + "\". Line has been ignored.");
            }

        }catch(IOException e)
        {
            System.out.println("[FATAL]: Could not get instance of logger");
        }
    }

    /**
     * Return the Crawl Delay specific to a <i>robots.txt</i> file.
     * @param url the URLString for which is needed Crawl Delay which could be present in a Robot
     * @return Crawl Delay if the site for which this method is called has one or 0 otherwise
     */
    public int getDelayForRobot(URLString url){

        for ( Robot iterator : robotsList ){
            if(iterator.getbaseUrlOfRobot().equals(url.getUrlString())){
                return iterator.getCrawlDelay();
            }
        }

        return 0;
    }
}
