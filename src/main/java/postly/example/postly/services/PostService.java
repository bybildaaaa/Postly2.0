package postly.example.postly.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import postly.example.postly.exceptions.ResourceNotFoundException;
import postly.example.postly.models.Post;
import postly.example.postly.repositories.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(int id) {
        return postRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Post not found")); // Исправлено
    }

    public List<Post> getPostsByUsername(String username) {
        List<Post> posts = postRepository.findByUsername(username);
        if (posts.isEmpty()) {
            throw new ResourceNotFoundException("No posts found for user: " + username);
        }
        return posts;
    }
}
