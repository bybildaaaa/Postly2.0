package postly.example.postly.services;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import postly.example.postly.cashe.CacheService;
import postly.example.postly.exceptions.ResourceNotFoundException;
import postly.example.postly.models.Post;
import postly.example.postly.models.User;
import postly.example.postly.repositories.PostRepository;
import postly.example.postly.repositories.UserRepository;
import postly.example.postly.util.ErrorMessages;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CacheService<Integer, Post> cacheService;

    @Autowired
    public PostService(
        PostRepository postRepository, UserRepository userRepository,
        CacheService<Integer, Post> cacheService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.cacheService = cacheService;
    }

    public List<Post> getPostsByUsername(String username) {
        return postRepository.findPostsByUsername(username);
    }

    public List<Post> getPostsByMinLikes(int likesCount) {
        return postRepository.findPostsByMinLikes(likesCount);
    }

    public int getLikesCount(int postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException(ErrorMessages.POST_NOT_FOUND);
        }
        return postRepository.getLikesCount(postId);
    }

    public Post createPost(int userId, String text) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        Post post = new Post();
        post.setUsername(user.getUsername());
        post.setPost(text);

        Post savedPost = postRepository.save(post);
        cacheService.put(savedPost.getId(), savedPost);
        logger.info("New post created and cached: postId={}", savedPost.getId());
        return savedPost;
    }

    public void deletePost(int postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException(ErrorMessages.POST_NOT_FOUND);
        }
        postRepository.deleteById(postId);
        cacheService.remove(postId);
        logger.info("Post deleted and removed from cache: postId={}", postId);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(int id) {
        logger.info("Fetching post from cache: postId={}", id);
        Post cachedPost = cacheService.get(id);
        if (cachedPost != null) {
            logger.info("Cache hit for postId={}", id);
            return cachedPost;
        }

        logger.info("Cache miss for postId={}, fetching from DB", id);
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.POST_NOT_FOUND));
        cacheService.put(id, post);
        logger.info("Post cached: postId={}", id);
        return post;
    }

    public void likePost(int postId, int userId) {
        Post post = getPostById(postId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!post.getLikedByUsers().contains(user)) {
            post.getLikedByUsers().add(user);
            postRepository.save(post);
            cacheService.put(postId, post);
            logger.info("Post liked and cache updated: postId={}, userId={}", postId, userId);
        }
    }

    public void unlikePost(int postId, int userId) {
        Post post = getPostById(postId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (post.getLikedByUsers().remove(user)) {
            postRepository.save(post);
            cacheService.put(postId, post);
            logger.info("Post unliked and cache updated: postId={}, userId={}", postId, userId);
        }
    }

    public List<User> getUsersWhoLikedPost(int postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.POST_NOT_FOUND));

        return post.getLikedByUsers();
    }

    public List<Post> getPostsByUserId(int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        return postRepository.findByUsername(user.getUsername());
    }

}
