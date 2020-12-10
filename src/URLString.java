import java.net.URL;

/**
 * This class aims to ensure that the URLs that
 * are found during the crawl operation are manipulated
 * along with the depth at which they are found.
 *
 * @author Ciobanu Cosmin-Marian
 */

public class URLString {
    /**
     * Members of the class
     * urlString is the link itself saved in a URL type variable
     * depth represents the depth reached after parsing the files
     */
    private URL urlString; // contains website specific link
    private int depth; // represents the depth in the file system

    /**
     * URLString class constructor
     * @param urlString The link we want to save
     * @param depth The position in the File System of this link
     */
    public URLString(URL urlString, int depth) {
        this.urlString = urlString;
        this.depth = depth;
    }

    public URL getUrlString() {
        return urlString;
    }

    public int getDepth() {
        return depth;
    }

    public void setUrlString(URL urlString) {
        this.urlString = urlString;
    }

    public void setDepth(int depth) {
        if (depth>0)
          this.depth = depth;
    }

}
