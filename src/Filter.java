public class Filter {

    private final String type;
    private final long size;

    public Filter(String type, long size) {
        this.type = type;
        this.size = size;
    }

    public boolean search(String directory) {
        return true;
    }

}
