import java.net.URL;

/**
 * This class is used to store the frequency of a word in a downloaded page
 *
 * @author Andreea Avram
 */

public class WordFreqs {
    /**
     * Members:
     * urlString - the URL of the page we inspect
     * word - the word we want to see the number of appearances of
     * freq - the number of appearances of the word @word
     * priority - parameter used to obtain more accurate results
     */
    public URL urlString;
    public String word;
    public int freq;
    public int priority;
}
