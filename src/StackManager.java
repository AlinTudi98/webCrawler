import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Stack;

public class StackManager {
    private static StackManager stackmanager_instance = null;
    private Stack<URLString> urlStack;
    private ArrayList<Robot> robotsList;
    private final Object lock = new Object();

    private StackManager(){
        urlStack = new Stack<URLString>();
        robotsList = new ArrayList<Robot>();
    }

    public static StackManager getInstance(){
        if(stackmanager_instance == null){
            stackmanager_instance = new StackManager();
        }
        return stackmanager_instance;
    }

    public void PushURL(URLString url){

        synchronized (lock){

            boolean availability=true;
            if (!robotsList.isEmpty()) {
                for ( Robot iterator : robotsList ) {
                    if (!iterator.verifyURL(url)) {
                        availability = false;
                        break;
                    }
                }
            }
            if (availability){
                urlStack.push(url);
            }
        }
    }

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

    public int getDelayForRobot(URLString url){

        for ( Robot iterator : robotsList ){
            if(iterator.getbaseUrlOfRobot().equals(url.getUrlString())){
                return iterator.getCrawlDelay();
            }
        }

        return 0;
    }
}
