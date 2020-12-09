import java.net.MalformedURLException;
import java.util.ArrayList;
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

  private StackManager(){
        urlStack = new Stack<URLString>();
        robotsList = new ArrayList<Robot>();
    }


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
     *
     * @param url is the new link which needs to be added in stack
     */
    public void PushURL(URLString url){

        synchronized (lock){

            boolean availability=true;
            if (!robotsList.isEmpty()) {
                for ( Robot iterator : robotsList ) { //verify if the new link is not in the disallowed list of a Robot
                    if (!iterator.verifyURL(url)) {
                        availability = false;
                        break;
                    }
                }
            }
            if (availability){ // if url link is a valid one it will be added in stack
                urlStack.push(url);
            }
        }
    }

    /**
     *
     * @return first URLString from stack or null if the stack is null
     */

    public URLString PopURL(){

        URLString firstElement;

        if (urlStack.empty()){
            return null;
        }

        synchronized (lock){
            firstElement = urlStack.pop();
            return firstElement;
        }
    }

    /**
     *  Will add a new Robot for one link if it is not already in list
     * @param url is the new link which might have a robots.txt file and is not already added
     */
    public void addRobot(URLString url){
        try {

            Robot newRobotToAdd = new Robot(url.getUrlString());

            synchronized (lock){
                for ( Robot iterator : robotsList){
                    if (iterator.getbaseUrlOfRobot().equals(url.getUrlString())){
                        return;
                    }
                }
                robotsList.add(newRobotToAdd);
            }
        }catch (MalformedURLException ignored){

        }
    }

    /**
     *
     * @param url the URLString for which is needed Crawl Delay which could be present in a Robot
     * @return Crawl Delay if the site for which this method is called has one or 0 otherwises
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
