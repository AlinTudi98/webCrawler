public class Main {
    public static void main(String[] args) {
        ;
    }

    private void readArgs(String[] args) {
        String[] posParams = {
                "-maxDepth=", "-dTypes=",
                "-ignoreRobots", "-config=", "-maxSize=", "-rootDir=",
        };

        Config config = loadConfig("config.txt");

        for (String iter : args) {
            if (iter.equalsIgnoreCase("-config=")) {
                config = loadConfig(iter.substring(8));
            }
        }

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-maxDepth=")) {
                config.maxDepth = Integer.parseInt(arg.substring(10));
            }

            if (arg.equalsIgnoreCase("-dTypes=")) {
                String types = arg.split("=")[1];
                config.dTypes = types.split(",");
            }

            if (arg.equalsIgnoreCase("-ignoreRobots")) {
                config.ignoreRobots = 1;
            }

            if (arg.equalsIgnoreCase("-maxSize=")) {
                config.dSizeLimit = Integer.parseInt(arg.substring(9));
            }
            if (arg.equalsIgnoreCase("-rootDir=")) {
                config.rootDir = arg.substring(9);
            }
        }
    }

    private Config loadConfig(String configPath) {
        return Config.getInstance(configPath);
    }

    private boolean processCommand(Config config, String[] args) {
        return true;
    }

}
