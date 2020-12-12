import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.Integer;

/**
 * This class stores the config information used in all the program
 * It reads the configuration file and stores the data in its members
 *
 * @author Andreea Avram
 */

public class Config {
    /**
     * All members are configuration parameters used by other classes
     * numThreads - number of threads that run the program
     * delay - the time we want to wait before starting the download process
     * rootDir - the root directory where the elements of the pages are stored
     * logLevel - how deep we want to log the crawlers activity
     * ignoreRobots - 0 if we want to ignore Robots.txt, 1 otherwise
     * logFilename - the path to the log file
     * maxDepth - how deep we want to go through the pages to download
     * dSizeLimit - the size limit of the documents we want to download
     * dTypes - the types of files we want to download
     */
    public int numThreads;
    public int delay;
    public String rootDir;
    public int logLevel;
    public int ignoreRobots;
    public String logFilename;
    public int maxDepth;
    public long dSizeLimit;
    public String[] dTypes = new String[20];

    private static Config ConfigInstance;

    final int numParams = 9;
    final String delims = "[= ]+";
    String[] tokens = new String[21];


    /**
     * Mapper class constructor
     * @filename the configuration file we want to load
     */

    private Config(String filename){
        try{
            File configFile = new File(filename);
            if (!configFile.exists()) {
                throw new FileException("Error with config file", LogCode.ERR);
            }
            Scanner reader = new Scanner(configFile);

            for (int i=0;i<numParams;i++){
                String data = reader.nextLine();
                tokens = data.split(delims);

                switch(tokens[0]){
                    case "numThreads":
                        numThreads = Integer.parseInt(tokens[1]);
                        break;
                    case "delay":
                        delay = Integer.parseInt(tokens[1]);
                        break;
                    case "rootDir":
                        rootDir = tokens[1];
                        break;
                    case "logLevel":
                        logLevel = Integer.parseInt(tokens[1]);
                        break;
                    case "ignoreRobots":
                        ignoreRobots = Integer.parseInt(tokens[1]);
                        break;
                    case "logFilename":
                        logFilename = tokens[1];
                        break;
                    case "maxDepth":
                        maxDepth = Integer.parseInt(tokens[1]);
                        break;
                    case "dSizeLimit":
                        dSizeLimit = Integer.parseInt(tokens[1]);
                        break;
                    case "dTypes":
                        for (int j=1;j< tokens.length;j++){
                            dTypes[j] = tokens[j];
                        }
                        break;
                }
            }
            reader.close();
        } catch (FileException | FileNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    public static Config getInstance(String ... filename)
    {
        if(ConfigInstance == null) {
            ConfigInstance = new Config(filename[0]);
        }
        return ConfigInstance;
    }

    public void printMembers(){
        System.out.println("Parametri de configurare sunt:\n");
        System.out.println(("numThreads = " + numThreads + "\n" +
                "delay = " + delay + "\n" +
                "rootDir = " + rootDir + "\n" +
                "logLevel = " + logLevel + "\n" +
                "ignoreRobots " + ignoreRobots + "\n" +
                "logFilename " + logFilename + "\n" +
                "maxDepth = " + maxDepth + "\n" +
                "dSizeLimit " + dSizeLimit + "\n" +
                "dTypes = "));
        for (int i=0;i<tokens.length;i++){
            System.out.println(dTypes[i] + " ");
        }
    }

    public void help(){
        System.out.println("Fisierul de configurare are urmatoarea structura:\n");
        System.out.println(("numThreads = <nr>\n" +
                            "delay = <nr>\n" +
                            "rootDir = <path>\n" +
                            "logLevel = <nr>\n" +
                            "ignoreRobots = <1/0>\n" +
                            "logFilename = <path>\n" +
                            "maxDepth = <nr>\n" +
                            "dSizeLimit = <nr>\n" +
                            "dTypes = <string> <string> <string> "));
    }
}
