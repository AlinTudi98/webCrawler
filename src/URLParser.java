import java.io.File;
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

    void parse(File urlFile){

    }

    void parse(String urlString){

    }
}
