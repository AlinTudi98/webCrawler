import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;

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

        for(String element : disallowUrls){
            if(element.equals(Url)){
                return false;
            }
        }

        return true;
    }

    public Robot() {
        disallowUrls = new ArrayList<String>();
        crawlDelay = 0;
    }

    public Robot(URL url) throws MalformedURLException{
        this();
        baseUrlOfRobot = url;

        try {
            String buffer;
            URLConnection connection;
            String site;
            BufferedReader inStream;
            String agent = "";

            site = url.toString()+"/robots.txt";
            url = new URL(site);
            connection = url.openConnection();
            connection.connect();

            inStream= new BufferedReader(new InputStreamReader(url.openStream()));
            while ((buffer = inStream.readLine()) != null){
                StringTokenizer token = new StringTokenizer(buffer," ");
                while (token.hasMoreElements()){
                    String tokenLine = token.nextToken();
                    if (tokenLine.equals("User-Agent:")) {
                        if(agent.equals("*")){
                            inStream.close();
                            return;
                        }
                        agent = token.nextToken();
                    }
                    if (tokenLine.equals("Crawl-delay:")) {
                        crawlDelay=Integer.parseInt(token.nextToken());
                    }
                    if (tokenLine.equals("Disallow:") && agent.equals("*") ) {
                        disallowUrls.add(token.nextToken());
                    }
                }
            }
            inStream.close();
        }catch (Exception exception){
            System.out.println("Robots inexistent on site: " + url.toString());
        }
    }

    public URL getbaseUrlOfRobot() {
        return baseUrlOfRobot;
    }

    public int getCrawlDelay() {
        return crawlDelay;
    }
}
