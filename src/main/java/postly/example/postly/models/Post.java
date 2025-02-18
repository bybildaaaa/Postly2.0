package postly.example.postly.models;

public class Post {
    private int id;
    private String username;
    private String text;
    private int likes;

    public Post(int id, String username, String text, int likes) {
        this.id = id;
        this.username = username;
        this.text = text;
        this.likes = likes;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPost() {
        return text;
    }

    public int getLikes() {
        return likes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPost(String text) {
        this.text = text;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
