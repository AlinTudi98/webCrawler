import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Sitemap {

    private final String path;

    public Sitemap(String path){
        this.path = path;
    }

    private void writeToFile(FileWriter fw, String message) throws IOException {
        //Helper function to prevent code duplication

        fw.write(message);
    }

    private String createTaber(int tabsNum) {
        return "\t".repeat(Math.max(0, tabsNum));
    }

    private int getTabsNumber(String parent){
        int tabNum = 1;
        for(int i = 0; i<parent.length(); i++)
        {
            if(parent.charAt(i) == '\\' || parent.charAt(i) == '/')
            {
                tabNum++;
            }
        }

        return tabNum;
    }

    private void listFilesInDirectory(String startDir, int tabsNum, FileWriter fw) throws IOException {

        Files.walk(Paths.get(startDir), 1)
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    try {
                        writeToFile(fw, createTaber(tabsNum)+filePath.getFileName() + "\n");
                    } catch (IOException e) {
                        System.out.println("WOOPSIE");
                    }
                });
    }

    private void listFiles(String startDir, FileWriter fw) throws IOException {
        Files.walk(Paths.get(startDir))
                .filter(Files::isDirectory)
                .forEach(filePath -> {
                    try {
                        if(!filePath.toString().equals(startDir)){
                            writeToFile(fw, createTaber(getTabsNumber(filePath.getParent().toString()))
                                        +filePath.getFileName()+"/\n");
                        }
                        else{
                            writeToFile(fw, filePath.getFileName()+"\n");
                        }

                        if(!filePath.toString().equals(startDir)){
                            listFilesInDirectory(filePath.toString(),
                                    getTabsNumber(filePath.getParent().toString())+1, fw);
                        }
                        else{
                            listFilesInDirectory(filePath.toString(),1, fw);
                        }

                    }catch (IOException e){
                        System.out.println("WOOPSIE");
                    }
                });
    }

    public boolean getSiteMap() throws IOException {
        Path fileName = Paths.get(this.path).getFileName();

        FileWriter fileWriter = new FileWriter(fileName.toString()+"_sitemap.txt");

        listFiles(this.path, fileWriter);

        fileWriter.close();

        return true;
    }
}
