package postly.example.postly.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import postly.example.postly.exceptions.ResourceNotFoundException;
import postly.example.postly.models.Post;
import postly.example.postly.models.User;
import postly.example.postly.repositories.PostRepository;
import postly.example.postly.repositories.UserRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Post createPost(String username, String text) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Post post = new Post();
        post.setUsername(username);
        post.setPost(text);
        post.setLikes(0);

        return postRepository.save(post);
    }

    public void deletePost(int postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found");
        }
        postRepository.deleteById(postId);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(int id) {
        return postRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    public void likePost(int postId, String username) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        // Проверяем, лайкал ли уже этот пользователь этот пост
        if (!post.getLikedByUsers().contains(user)) {
            post.getLikedByUsers().add(user);
            post.setLikes(
                post.getLikedByUsers().size()); // Количество лайков = количеству пользователей
            postRepository.save(post);
        }
    }

    public void unlikePost(int postId, String username) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if (post.getLikedByUsers().remove(user)) {
            post.setLikes(post.getLikedByUsers().size());
            postRepository.save(post);
        }
    }

    public List<User> getUsersWhoLikedPost(int postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return post.getLikedByUsers();
    }

    public List<Post> getPostsByUsername(String username) {
        List<Post> posts = postRepository.findByUsername(username);
        if (posts.isEmpty()) {
            throw new ResourceNotFoundException("No posts found for user: " + username);
        }
        return posts;
    }
}
