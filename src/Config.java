public class Config {



    public int numThreads;
    public int delay;
    public String rootDir;
    public int logLevel;
    public boolean ignoreRobots;
    public String logFilename;
    public long dSizeLimit;

    private static Config ConfigInstance;

    private Config(String filename){

    }

    public Config getInstance(String filename)
    {
        if(ConfigInstance == null) {
            ConfigInstance = new Config(filename);
        }

        return ConfigInstance;
    }
}
