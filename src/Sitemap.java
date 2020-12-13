import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The purpose of this class is to create the sitemap of a recently
 * crawled site and write it to a file, in an ordered format,
 * based on the files and directories contained in the downloaded
 * content.
 *
 * @author Ghita Alexandru-Andrei
 */

public class Sitemap {

    /**
     * Members of class Sitemap
     * path: the path to the directory that contains the
     * downloaded items
     */

    private final String path;

    public Sitemap(String path) {
        this.path = path;
    }

    /**
     * Helper function to avoid code dupplication
     */

    private void writeToFile(FileWriter fileWriter, String message) throws IOException {
        fileWriter.write(message);
    }

    /**
     * Function used to add tabs for a pretty print, based
     * on the files hierarchy
     *
     * @param tabsNum the level that the folder/file is on
     * @return a string filled with the required number of tabs
     */

    private String createTaber(int tabsNum) {
        return "\t".repeat(Math.max(0, tabsNum));
    }

    /**
     * @param parent contains the path from the parent to the file in cause
     * @return the level the child is on, based on the number of slashes found
     * in the parent path
     */

    private int getTabsNumber(String parent) {
        int tabNum = 1; //only the root folder is displayed on the first level
        for (int i = 0; i < parent.length(); i++) {
            if (parent.charAt(i) == '\\' || parent.charAt(i) == '/') { //Either Unix or Windows format
                tabNum++;
            }
        }

        return tabNum;
    }

    /**
     * Lists all the regular files inside a directory, excluding directories.
     * Method uses Files.walk for both Unix and Windows compatibility.
     *
     * @param startDir   the directory from which the files are listed
     * @param tabsNum    the level the base directory is on
     * @param fileWriter sitemap output file
     * @throws IOException if unable to write to output file or
     *                     <b>FATAL</b> exception if unable to get an instance
     *                     of the Logger used to log the previous exception.
     */

    private void listFilesInDirectory(String startDir, int tabsNum, FileWriter fileWriter) throws IOException {
        Files.walk(Paths.get(startDir), 1) // maxDepth is set to 1 no to list nested directories content
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    try {
                        writeToFile(fileWriter, createTaber(tabsNum) + filePath.getFileName() + "\n");
                    } catch (IOException e) {
                        try {
                            Logger.getInstance().log(LogCode.FATAL,
                                    "Sitemap was unable to write to output file!");
                        } catch (IOException exp) {
                            System.out.println("Unexpected error occurred! Logger cannot be created!");
                        }
                    }
                });
    }

    /**
     * Method lists all the directories located in the base directory,
     * calling {@link Sitemap#listFilesInDirectory(String, int, FileWriter)}
     * for each one of them
     *
     * @param startDir   the main directory for which the Sitemap was invoked
     * @param fileWriter sitemap output file
     * @throws IOException if unable to write to output file or
     *                     <b>FATAL</b> exception if unable to get an instance
     *                     of the Logger used to log the previous exception.
     */

    private void listFiles(String startDir, FileWriter fileWriter) throws IOException {
        Files.walk(Paths.get(startDir))
                .filter(Files::isDirectory)
                .forEach(filePath -> {
                    try {
                        if (!filePath.toString().equals(startDir)) {
                            writeToFile(fileWriter, createTaber(getTabsNumber(filePath.getParent().toString()))
                                    + filePath.getFileName() + "/\n");
                        } else {
                            writeToFile(fileWriter, filePath.getFileName() + "\n");
                        }

                        if (!filePath.toString().equals(startDir)) {
                            listFilesInDirectory(filePath.toString(),
                                    getTabsNumber(filePath.getParent().toString()) + 1, fileWriter);
                        } else {
                            listFilesInDirectory(filePath.toString(), 1, fileWriter);
                        }

                    } catch (IOException e) {
                        try {
                            Logger.getInstance().log(LogCode.FATAL,
                                    "Sitemap was unable to write to output file!");
                        } catch (IOException exp) {
                            System.out.println("Unexpected error occurred! Logger cannot be created!");
                        }
                    }
                });
    }

    /**
     * @return true if the operation was successful, or logs the error
     * @throws IOException if it's unable to create a <i>FileWriter</i> from
     *                     the file given
     */

    public boolean getSiteMap() throws IOException {
        Path fileName = Paths.get(this.path).getFileName();

        FileWriter fileWriter = new FileWriter(fileName.toString() + "_sitemap.txt");
        //creates a file with the following format: <root_directory>_sitemap.txt

        listFiles(this.path, fileWriter);

        fileWriter.close();

        return true;
    }
}
