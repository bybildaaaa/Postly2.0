package postly.example.postly.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import postly.example.postly.models.Post;

@Service
public class PostService {

    private final List<Post> posts = new ArrayList<>();

    public PostService() {
        posts.add(new Post(1, "Biba", "Interesting history", 74893));
        posts.add(new Post(2, "Boba", "BlaBlaBla", 3));
    }

    public List<Post> getAllPosts() {
        return posts;
    }

    public Post getPostById(int id) {
        return posts.stream()
                .filter(post -> post.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Post> getPostsByUsername(String username) {
        return posts.stream()
                .filter(post -> post.getUsername().equalsIgnoreCase(username))
                .toList();
    }
}
