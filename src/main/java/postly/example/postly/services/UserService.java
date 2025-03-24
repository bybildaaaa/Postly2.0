package postly.example.postly.services;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import postly.example.postly.cashe.CacheService;
import postly.example.postly.exceptions.ResourceNotFoundException;
import postly.example.postly.exceptions.UserAlreadyExistsException;
import postly.example.postly.models.Comment;
import postly.example.postly.models.Post;
import postly.example.postly.models.User;
import postly.example.postly.repositories.CommentRepository;
import postly.example.postly.repositories.PostRepository;
import postly.example.postly.repositories.UserRepository;
import postly.example.postly.util.ErrorMessages;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CacheService<Integer, Post> cacheService;



    @Autowired
  public UserService(
        UserRepository userRepository,
        PostRepository postRepository,
        CommentRepository commentRepository,
        CacheService<Integer, Post> cacheService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.cacheService = cacheService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));
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
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        List<Post> postsLikedByUser = postRepository.findAll()
            .stream()
            .filter(post -> post.getLikedByUsers().contains(user))
            .toList();

        for (Post post : postsLikedByUser) {
            post.getLikedByUsers().remove(user);
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

        String oldUsername = user.getUsername();
        user.setUsername(newUsername);
        userRepository.save(user);

        updatePostsAndComments(oldUsername, newUsername);

        return user;
    }

    private void updatePostsAndComments(String oldUsername, String newUsername) {
        List<Post> posts = postRepository.findPostsByUsername(oldUsername);
        for (Post post : posts) {
            post.setUsername(newUsername);
            postRepository.save(post);

            if (cacheService.get(post.getId()) != null) {
                cacheService.put(post.getId(), post);
            }
        }

        List<Comment> comments = commentRepository.findAll().stream()
            .filter(c -> c.getUsername().equals(oldUsername))
            .toList();
        for (Comment comment : comments) {
            comment.setUsername(newUsername);
            commentRepository.save(comment);
        }

        logger.info("Updated username '{}' to '{}'", oldUsername, newUsername);
    }

}