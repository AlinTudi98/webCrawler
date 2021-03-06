import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for indexing the words from the downloaded pages
 * and for showing the result of the search depending on the indexing and relevance
 *
 * @author Andreea Avram
 */

public class WordIndexer {
    /**
     * Members:
     * wordString - the string got from the program parameters that contains the word/words we want to search
     * freqList - the list of the the frequencies of the searched words and the correspondig site
     * rootDir - the root directory where we want to start the search
     *
     */
    public String wordString;
    public List<WordFreqs> freqsList = new ArrayList<WordFreqs>();
    public String rootDir;


    final String delims = "[ ,.?!;:()<>\"]+";
    List<String> searchFiles = new ArrayList<String>();
    String[] tokens = new String[200];
    String[] words = new String[200];
    int numWords;

    /**
     * This is the function that does the counting of the appearances for a given page
     * @param filename - the filename of the downloaded page we search in
     */
    public void indexPage(String filename){
        int freq=0;

        try{
            File file = new File(filename);
            if (!file.exists()){
                throw new FileException("Error reading input file",LogCode.ERR);
            }

                    Scanner reader = new Scanner(file);
            while (reader.hasNextLine()){
                String data = reader.nextLine();
                tokens = data.split(delims);

                for (int i=0;i<numWords;i++){
                    for (int j=0;j<tokens.length;j++){
                        if ((words[i].toLowerCase()).equals(tokens[j].toLowerCase())){
                            //System.out.println(tokens[j-1] + " "+tokens[j]);
                            freq++;
                        }
                    }
                }
            }
            freqsList.add(new WordFreqs(filename,freq,1));
        }
        catch (FileException | FileNotFoundException e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * This is the function that starts the counting for every page
     * @param root - the root directory where we begin the search
     */
    public void index(String root){

        File f = new File(root);
        String[] pathnames;
        pathnames = f.list();

        Pattern pHtml = Pattern.compile(".*.html?");
        Pattern pDirectory = Pattern.compile("^[^.]*$");

        if (pathnames != null){
            for (String pathname : pathnames){
                Matcher m1 = pHtml.matcher(pathname);
                Matcher m2 = pDirectory.matcher(pathname);
                if (m1.find()){
                    if (!searchFiles.contains(pathname)){
                        String newName = root + "/" + pathname;
                        searchFiles.add(newName);
                    }
                }
                else {
                    File tmp = new File(root + "/" +pathname);
                    if (tmp.isDirectory()) {
                        String newPathname = root + "/" + pathname;
                        index(newPathname);
                    }
                }
            }
        }
    }

    /**
     * This is the method we call from our Main function
     * @param pWordString - the string that contains the words we want to search for
     * @param pRootDir - the root directory where the search begins
     */
    public void search(String pWordString, String pRootDir){
        wordString=pWordString;
        rootDir=pRootDir;

        words = wordString.split("[ ]+");
        numWords = words.length;

        index(pRootDir);
        for (int i=0;i<searchFiles.size();i++){
            indexPage(searchFiles.get(i));
        }
        printSorted();
    }

    /**
     * This is the function that displays on the screen the result of the search
     */
    void printSorted() {
        Collections.sort(freqsList);
        Collections.reverse(freqsList);
        System.out.println("Paginile care contin cuvintele cautate in ordinea relevantei sunt:");
        for (int i = 0; i < freqsList.size(); i++) {
            if (freqsList.get(i).freq != 0)
                System.out.println(freqsList.get(i).urlString);
        }
    }
}
