
public class PageCrawler extends Thread{

    private int maxDepth;
    private long maxSize;
    private URLString currUrl;

    public PageCrawler(int depth, long size) {
        this.maxDepth = depth;
        this.maxSize = size;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() { }

    private int getCrawlDelay() {
        return 0;
    }

    private String parse(String content) {
        return "";
    }

    private boolean makeFS(String finalContent) {
        return true;
    }

}
