package postly.example.postly.models;

public class Post {
    private int id;
    private String username;
    private String post;
    private int likes;

    public Post(int id, String username, String post, int likes) {
        this.id = id;
        this.username = username;
        this.post = post;
        this.likes = likes;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPost() {
        return post;
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

    public void setPost(String post) {
        this.post = post;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
