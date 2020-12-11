/**
 * This class is used to store the frequency of a word in a downloaded page
 *
 * @author Andreea Avram
 */

public class WordFreqs implements Comparable<WordFreqs>{
    /**
     * Members:
     * urlString - the URL of the page we inspect
     * word - the word we want to see the number of appearances of
     * freq - the number of appearances of the word @word
     * priority - parameter used to obtain more accurate results
     */
    public String urlString;
    public int freq;
    public int priority;

    WordFreqs(String pUrlString, int pFreq, int pPriority){
        urlString = pUrlString;
        freq=pFreq;
        priority=pPriority;
    }

    public int getFreq(){
        return freq;
    }

    @Override
    public int compareTo(WordFreqs o) {
        return Integer.valueOf(this.getFreq()).compareTo(o.getFreq());
    }
}
