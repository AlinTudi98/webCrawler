
public class PageCrawler extends Thread{

    private int maxDepth;
    private long maxSize;
    private URLString currUrl;

    public PageCrawler(int depth, long size) {
        this.maxDepth = depth;
        this.maxSize = size;
    }

}
