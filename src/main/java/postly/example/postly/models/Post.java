package postly.example.postly.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String text;
    private int likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public Post() {
    }

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
