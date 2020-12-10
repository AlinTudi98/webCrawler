import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
     *                 for a link like <i>http://example.com/robots.txt</i> the value of this field should be
     *                 <i>http://example.com</i>.
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

    public Robot() {
        disallowUrls = new ArrayList<String>();
        crawlDelay = 0;
    }

    /**
     * The Robot class constructor to check if it exists and then start
     * creating the member variables.
     * @param url the link for which we want to create an object for Robot
     * @throws Exception is used if we don't have a <i>Robots.txt</i> file linked with our URL
     */
    public Robot(URL url) throws MalformedURLException{
        /* Calling the constructor because crawlDelay needs to be 0 if it is not present in robots.txt */
        this();
        baseUrlOfRobot = url;

        try {
            /**
             * buffer: where is saved the content of <i>robots.txt</i> file
             * connection: used to connect at our link
             * site: the new site, with <b>robots.txt</b> path.
             * agent: current Agent from robots.txt file for which we verify rules
             */
            String buffer;
            URLConnection connection;
            String site;
            BufferedReader inStream;
            String agent = "";

            site = url.toString()+"robots.txt";
            url = new URL(site);
            connection = url.openConnection();
            connection.connect(); //connect to our site, having "robots.txt" in the final

            inStream= new BufferedReader(new InputStreamReader(url.openStream()));
            while ((buffer = inStream.readLine()) != null) { // while we still have lines to read from file
                StringTokenizer token = new StringTokenizer(buffer," "); //take one word at a time
                while (token.hasMoreElements()){ //while there are words to verify
                    String tokenLine = token.nextToken();
                    if (tokenLine.equals("User-agent:")) { //if we have an User-Agent field
                        if(agent.equals("*")){ //if we already read rules for "*" Agent
                            inStream.close();
                            return; // reading is closed
                        }
                        agent = token.nextToken();
                    }
                    if (tokenLine.equals("Crawl-delay:")) { //if we need to take crawl delay field
                        crawlDelay=Integer.parseInt(token.nextToken());
                    }
                    if (tokenLine.equals("Disallow:") && agent.equals("*") ) { // we want to read disallow rules only for "*" Agent
                        disallowUrls.add(token.nextToken());
                    }
                }
            }
            inStream.close();
        }catch (Exception exception){
            try {

                Logger.getInstance().log(LogCode.WARN, "[WARN] Robot: URL: \"" + url.toString() + "\" doesn't have robots.txt file.");

            }catch(IOException e)
            {
                System.out.println("[FATAL]: Could not get instance of logger");
            }
        }
    }

    public URL getbaseUrlOfRobot() {
        return baseUrlOfRobot;
    }

    /**
     *
     * @return return crawlDelay specific for the current site
     *         it can be 0 if the current site doesn't have robots.txt file
     *         or in this file is not specified Crawl Delay
     */

    public int getCrawlDelay() {
        return crawlDelay;
    }

    /**
     *
     * @param Url link to be checked in the list of valid urls for download
     * @return True if searched link is not present in list and False otherwise
     */
    public boolean verifyURL(URLString Url){
        //Extract from URL the relative link
        String urlToVerify = Url.getUrlString().getFile();

        //Verify every rule with disallow
        for(String element : disallowUrls) {
            String elem = element.replace("*",".*"); //change * in .* to work as a REGEX
            elem = elem.replace("?","\\?"); //make ? /? because it should not enter in REGEX

            Pattern pattern = Pattern.compile(elem);
            Matcher matcher = pattern.matcher(urlToVerify);
            if (matcher.matches()) { // verify if changed REGEX is a match
                return false;
            }
        }

        return true;
    }

}
