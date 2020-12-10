import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Filter {

    private final String type;
    private final long size;

    public Filter(String type, long size) {
        this.type = type;
        this.size = size;
    }

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
