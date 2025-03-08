package postly.example.postly.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    public List<Post> getPostsByUsername(String username) {
        List<Post> filteredPosts = posts.stream()
            .filter(post -> post.getUsername().equalsIgnoreCase(username))
            .collect(Collectors.toList());

        if (filteredPosts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No posts found for user: " + username);
        }

        return filteredPosts;
    }
}
