import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The purpose of this class is to retrieve certain files in a given
 * path that match the criteria of size and type.
 *
 * @author Ghita Alexandru-Andrei
 *
 */

public class Filter {

    /**
     * Members of class Filter
     * type: the type of the file that we are querying for. Example:
     *       txt, pdf, html
     * size: the maximum size that the file must have in order to match
     *       the criteria
     */

    private final String type;
    private final long size;

    public Filter(String type, long size) {
        this.type = type;
        this.size = size;
    }

    /**
     * Method used to retrieve a file extension. For example
     * if we have "output.pdf" it will retrieve "pdf". If no
     * extension is present, it will assume it is a <i>.txt</i> file
     *
     * @param fileName the name of the file containing the extension
     * @return the extension as a <i>String</i>
     */

    private String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
            return extension;
        }
        else {
            return "txt";
        }

    }

    /**
     *
     * Method used to print the results consisting of the file name, size and location, that match
     * the <b>type</b> and <b>size</b> arguments.
     *
     * @param directory the directory given as the location to search for files that
     *                  match the criteria
     * @return true if the operation has not encountered any errors
     * @throws IOException if the path cannot be accessed by the FileWriter
     */

    public boolean search(String directory) throws IOException {
        Files.walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    try {
                        if(Files.size(filePath) <= size && getFileExtension(filePath.getFileName().toString()).equals(type)) {
                            System.out.println("Name: "+filePath.getFileName().toString() +
                                    "; Size: " + Files.size(filePath) + "(bytes); " +
                                    "Directory: " + filePath.getParent().toString());
                        }
                    } catch (IOException e) {
                        try {
                            Logger.getInstance().log(LogCode.FATAL, "Filter was unable to access given path!");
                        } catch (IOException ioException) {
                            System.out.println("Unexpected error occurred! Logger cannot be created!");
                        }
                    }
                });

        return true;
    }
}
