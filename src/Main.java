public class Main {
    public static void main(String[] args) {
        ;
    }

    private void readArgs(String[] args, Config config) {
        String[] posParams = {"crawl","sitemap","list","search",
                "-startLinks=","-inFile=","-maxDepth=","-dTypes=",
                "-ignoreRobots","-config=","-maxSize=","-rootDir=",
                "-keyWords="};

        for(String iter: args)
        {

        }
    }

    private Config loadConfig(String configPath){
        return Config.getInstance(configPath);
    }

    private boolean processCommand(Config config, String[] args)
    {
        return true;
    }

}
