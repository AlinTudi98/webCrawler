import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class deals with the extraction of information from
 * the "robots.txt" type files that the sites may have
 * and with the provision of this information to the other components.
 *
 * @author Ciobanu Cosmin Marian
 */

public class Robot {

    private URL baseUrlOfRobot;
    private ArrayList<String> disallowUrls;
    private int crawlDelay;

    public boolean verifyURL(String Url){

        return true;
    }

    public Robot() {
    }

    public Robot(URL url) throws MalformedURLException{
    }

    public URL getbaseUrlOfRobot() {

        return baseUrlOfRobot;
    }

    public int getCrawlDelay() {

        return crawlDelay;
    }
}
