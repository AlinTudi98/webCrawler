import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        readArgs(args);
        try {
            FileWriter fw = new FileWriter("log.txt");
            Logger.getInstance(Config.getInstance().logLevel, fw);
            processCommand(args);
            fw.close();
        } catch (IOException e) {
            System.out.println("[Fatal] Could not get logger instance.\n");
        }
    }

    private static void readArgs(String[] args) {
        String[] posParams = {
                "-maxDepth=", "-dTypes=",
                "-ignoreRobots", "-config=", "-maxSize=", "-rootDir=",
        };

        Config config = loadConfig("config.txt");

        for (String iter : args) {
            if (iter.contains("-config=")) {
                config = loadConfig(iter.substring(8));
            }
        }

        for (String arg : args) {
            if (arg.contains("-maxDepth=")) {
                config.maxDepth = Integer.parseInt(arg.substring(10));
            }

            if (arg.contains("-dTypes=")) {
                String types = arg.split("=")[1];
                config.dTypes = types.split(" ");
            }

            if (arg.contains("-ignoreRobots")) {
                config.ignoreRobots = 1;
            }

            if (arg.contains("-maxSize=")) {
                config.dSizeLimit = Integer.parseInt(arg.substring(9));
            }
            if (arg.contains("-rootDir=")) {
                config.rootDir = arg.substring(9);
            }
        }
    }

    private static Config loadConfig(String configPath) {
        return Config.getInstance(configPath);
    }

    private static boolean processCommand(String[] args) {
        String command = args[1];

        switch (command.toLowerCase()) {
            case "crawl":
                for (String arg : args) {
                    if (arg.contains("-dLink=")) {
                        String urlString = arg.substring(8, arg.length() - 1);
                        URLParser parser = new URLParser();
                        parser.parse(urlString);
                    }
                    if (arg.contains("-dLinksFile=")) {
                        String filename = arg.substring(12);
                        URLParser parser = new URLParser();
                        File file = new File(filename);
                        parser.parse(file);
                    }
                }

                ArrayList<PageCrawler> crawlerList = new ArrayList<PageCrawler>();
                for (int i = 0; i < Config.getInstance().numThreads; i++) {
                    PageCrawler tmp = new PageCrawler(Config.getInstance().maxDepth, Config.getInstance().dSizeLimit * 1024);
                    tmp.start();
                    crawlerList.add(tmp);
                }
                for (PageCrawler iter : crawlerList) {
                    try {
                        iter.join();
                    } catch (InterruptedException e) {
                        ;
                    }
                }
                return true;

            case "sitemap":
                Sitemap map = new Sitemap(Config.getInstance().rootDir);
                try {
                    map.getSiteMap();
                } catch (IOException e) {
                    try {
                        Logger.getInstance().log(LogCode.FATAL, "Sitemap: Could not create sitemap.txt file.");
                        return false;
                    } catch (IOException f) {
                        System.out.println("[Fatal] Could not get logger instance.\n");
                        return false;
                    }
                }
                return true;

            case "search":
                String wordsString = null;
                for (String arg : args) {
                    if (arg.contains("-words=")) {
                        wordsString = arg.substring(7);
                    }
                }
                WordIndexer indexer = new WordIndexer();
                if (wordsString == null) {
                    try {
                        Logger.getInstance().log(LogCode.FATAL, "Empty words string");
                        return false;
                    } catch (IOException e) {
                        System.out.println("[Fatal] Could not get logger instance.\n");
                        return false;
                    }
                }
                indexer.search(wordsString, Config.getInstance().rootDir);
                return true;

            case "list":
                for (String type : Config.getInstance().dTypes) {
                    Filter filter = new Filter(type, Config.getInstance().dSizeLimit * 1024);
                    try {
                        filter.search(Config.getInstance().rootDir);
                    } catch (IOException e) {
                        try {
                            Logger.getInstance().log(LogCode.FATAL, "Could not access target directory.");
                            return false;
                        } catch (IOException f) {
                            System.out.println("[Fatal] Could not get logger instance.\n");
                            return false;
                        }
                    }
                }
                return true;
            default:
                return false;
        }

    }

}
