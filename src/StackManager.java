import java.util.ArrayList;
import java.util.Stack;

public class StackManager {
    private static StackManager stackmanager_instance = null;
    private Stack<URLString> urlStack;
    private ArrayList<Robot> robotsList;
    private final Object lock = new Object();
    private StackManager(){ ; }

    public static StackManager getInstance(){
        if(stackmanager_instance == null){
            stackmanager_instance = new StackManager();
        }
        return stackmanager_instance;
    }

    public void PushURL(URLString url){
        synchronized (lock){

        }
    }

    public URLString PopURL(){
        synchronized (lock){

        }
        return null;
    }

    public void addRobot(URLString url){
        synchronized (lock){

        }
    }

    public int getDelayForRobot(URLString url){
        return 0;
    }
}
