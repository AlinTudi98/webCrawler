import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.*;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class URLParser {

    boolean validateURL(String url){

        String urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern urlPattern = Pattern.compile(urlRegex);
        Matcher urlMatcher = urlPattern.matcher(url);

        if(urlMatcher.find()){
            String testUrl = urlMatcher.group(0);
            if(url.equalsIgnoreCase(testUrl))
                return true;
            return false;
        }
        return false;
    }

    public void parse(File urlFile) {
        StackManager stackManager = StackManager.getInstance();
        try {
            Scanner reader = new Scanner(urlFile);


            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                if (validateURL(line)) {
                    try {
                        URL url = new URL(line);
                        URLString urlString = new URLString(url, 0);
                        stackManager.PushURL(urlString);
                        //Logger.log(Pushed line);
                    } catch (MalformedURLException e) {
                        //Logger.log(Ignored line);
                    }
                } else {
                    //Logger.log(Ignored line);
                }

            }
            reader.close();
        }
        catch(FileNotFoundException ee)
        {
            //Logger.log(Fatal, file not found)
        }

    }

    public void parse(String urlString){
        StackManager stackManager = StackManager.getInstance();

        for( String line: urlString.split(","))
        {
            if (validateURL(line)) {
                try {
                    URL url = new URL(line);
                    URLString urlStr = new URLString(url, 0);
                    stackManager.PushURL(urlStr);
                    //logger.log(Pushed line);
                } catch (MalformedURLException e) {
                    //Logger.log(Ignored line);
                }
            } else {
                //Logger.log(Ignored line);
            }
        }

    }
}
