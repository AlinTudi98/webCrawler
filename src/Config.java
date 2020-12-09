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

    public void help(){
        System.out.println("Fisierul de configurare are urmatoarea structura:\n");
        System.out.println(("numThreads = <nr>\n" +
                            "delay = <nr>\n" +
                            "rootDir = <path>\n" +
                            "logLevel = <nr>\n" +
                            "ignoreRobots = <1/0>\n" +
                            "logFilename = <path>\n" +
                            "dSizeLimit = <nr>"));
    }
    

}
