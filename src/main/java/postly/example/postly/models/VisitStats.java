package postly.example.postly.models;

public class VisitStats {
    private final String url;
    private final long visitCount;

    public VisitStats(String url, long visitCount) {
        this.url = url;
        this.visitCount = visitCount;
    }

    public String getUrl() {
        return url;
    }

    public long getVisitCount() {
        return visitCount;
    }
}