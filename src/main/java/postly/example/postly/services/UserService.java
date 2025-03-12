package postly.example.postly.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import postly.example.postly.exceptions.ResourceNotFoundException;
import postly.example.postly.exceptions.UserAlreadyExistsException;
import postly.example.postly.models.Comment;
import postly.example.postly.models.Post;
import postly.example.postly.models.User;
import postly.example.postly.repositories.CommentRepository;
import postly.example.postly.repositories.PostRepository;
import postly.example.postly.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
  public UserService(
        UserRepository userRepository,
        PostRepository postRepository,
        CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User createUser(String username) {
        if (userRepository.findByUsername(username) != null) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }

        User user = new User();
        user.setUsername(username);
        return userRepository.save(user);
    }

    public void deleteUser(int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Post> postsLikedByUser = postRepository.findAll()
            .stream()
            .filter(post -> post.getLikedByUsers().contains(user))
            .toList();

        for (Post post : postsLikedByUser) {
            post.getLikedByUsers().remove(user);
            post.setLikes(post.getLikedByUsers().size());
            postRepository.save(post);
        }

        List<Post> posts = postRepository.findByUsername(user.getUsername());
        postRepository.deleteAll(posts);

        List<Comment> comments = commentRepository.findAll()
            .stream().filter(c -> c.getUsername().equals(user.getUsername())).toList();
        commentRepository.deleteAll(comments);

        userRepository.delete(user);
    }

    @Transactional
  public User updateUsername(int userId, String newUsername) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userRepository.findByUsername(newUsername) != null) {
            throw new UserAlreadyExistsException("Username already taken");
        }

        user.setUsername(newUsername);
        return userRepository.save(user);
    }

}