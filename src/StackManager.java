import java.net.URL;
import java.util.Stack;

public class StackManager {
    Stack<URLString> urlStack;
    private static StackManager stackmanager_instance = null;

    private StackManager(){ ; }

    public static StackManager getInstance(){
        if(stackmanager_instance == null){
            stackmanager_instance = new StackManager();
        }
        return stackmanager_instance;
    }
}
