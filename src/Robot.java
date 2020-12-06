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

    /**
     * Members of class Robot
     * baseUrlOfRobot: represents base link of this robot file, for example
     *                 for a link like http://example.com/robots.txt the value of this field should be
     *                 http://example.com.
     * disallowUrls: is a list with all the sites specific for this link which cannot be downloaded by
     *               our crawler.
     * crawlDelay: is a value that suggests the time that the crawler can wait maximum for a certain
     *             page that it wants to download. it is not mandatory for this value to be present,
     *             so it can have the value 0, this highlighting that all the value from the
     *             configuration file is used.
     */

    private URL baseUrlOfRobot;
    private ArrayList<String> disallowUrls;
    private int crawlDelay;

    /**
     *
     * @param Url link to be checked in the list of valid urls for download
     * @return True if searched link is not present in list and False otherwise
     */
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

    /**
     *
     * @param url the link for which we want to create an object for Robot
     * @throws Exception is used for the case in which we don't have a Robots.txt file linked with our link
     */
    public Robot(URL url) throws MalformedURLException{
        /* Calling the constructor because crawlDelay needs to be 0 if it is not present in robots.txt */
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
            System.out.println("Robots absent on site: " + url.toString());
        }
    }

    public URL getbaseUrlOfRobot() {
        return baseUrlOfRobot;
    }

    public int getCrawlDelay() {
        return crawlDelay;
    }
}
