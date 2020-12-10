import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static jdk.javadoc.internal.doclets.toolkit.util.Utils.toLowerCase;

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
     * freqList - the list of the searched words and their frequencies
     * rootDir - the root directory where we want to start the search
     *
     */
    public String wordString;
    public WordFreqs[] freqsList;
    public String rootDir;
    final String delims = "[ ,.?!;:()<>\"]+";
    String[] tokens = new String[200];
    String[] words = new String[200];
    int numWords;


    public void indexPage(String filename){
        //ToDo: implement the search for the full wordString

        try{
            File file = new File(filename);
            if (!file.exists()){
                throw new FileException("Error reading input file",ErrorCode.ERR);
            }
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()){
                String data = reader.nextLine();
                tokens = data.split(delims);

                for (int i=0;i<numWords;i++){
                    for (int j=0;j<tokens.length;j++){
                        if ((words[i].toLowerCase()).equals(tokens[j].toLowerCase())){
                            //System.out.println(tokens[j-1] + " "+tokens[j]);
                            freqsList[i].freq ++;
                        }
                    }
                }
            }
        }
        catch (FileException | FileNotFoundException e){
            System.out.println(e.getMessage());
        }

    }

    public void index(){

    }

    public void search(String pWordString, String pRootDir){
        wordString=pWordString;
        rootDir=pRootDir;

        words = wordString.split("[ ]+");
        numWords = words.length;
        freqsList = new WordFreqs[20];

        for (int i=0;i<numWords;i++){
            freqsList[i] = new WordFreqs();
            freqsList[i].word = words[i];
            freqsList[i].urlString = pRootDir; //ToDo: Change that
            freqsList[i].priority = 1;
            freqsList[i].freq = 0;
        }

        indexPage(pRootDir);
        printSorted();
    }

    void printSorted(){
        for (int i=0;i<numWords;i++){
            System.out.println(freqsList[i].word + " = " + freqsList[i].freq);
        }
    }

}
