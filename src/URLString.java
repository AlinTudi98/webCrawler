import java.net.URL;

/**
 * @author Ciobanu Cosmin-Marian
 */

public class URLString {

    private URL urlString; // contains website specific link
    private int depth; // represents the depth in the file system

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
