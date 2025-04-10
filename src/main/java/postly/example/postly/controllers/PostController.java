package postly.example.postly.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import postly.example.postly.exceptions.InvalidRequestException;
import postly.example.postly.exceptions.ResourceNotFoundException;
import postly.example.postly.models.Post;
import postly.example.postly.models.User;
import postly.example.postly.services.PostService;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{postId}/likes/count")
    public int getLikesCount(@PathVariable int postId) {
        if (postId <= 0) {
            throw new InvalidRequestException("Invalid post ID");
        }
        return postService.getLikesCount(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@RequestParam int userId, @RequestParam String text) {
        if (userId <= 0 || text.isBlank()) {
            throw new InvalidRequestException("Invalid input data");
        }
        return postService.createPost(userId, text);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable int postId) {
        postService.deletePost(postId);
    }

    @GetMapping// берёт параметры из URL
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable int id) {
        return postService.getPostById(id);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable int postId, @RequestParam int userId) {
        if (postId <= 0 || userId <= 0) {
            throw new InvalidRequestException("Invalid input data");
        }
        postService.likePost(postId, userId);
        return ResponseEntity.ok("Post liked successfully");
    }

    @DeleteMapping("/{postId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikePost(@PathVariable int postId, @RequestParam int userId) {
        postService.unlikePost(postId, userId);
    }

    @GetMapping("/{postId}/likes")
    public List<User> getUsersWhoLikedPost(@PathVariable int postId) {
        return postService.getUsersWhoLikedPost(postId);
    }

    @GetMapping(params = "userId")
    public List<Post> getPostsByUserId(@RequestParam int userId) {
        return postService.getPostsByUserId(userId);
    }

    @GetMapping("/filter/username")
    public List<Post> getPostsByUsername(@RequestParam String username) {
        return postService.getPostsByUsername(username);
    }

    @GetMapping("/filter/min-likes")
    public List<Post> getPostsByMinLikes(@RequestParam int likesCount) {
        return postService.getPostsByMinLikes(likesCount);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ex.getMessage();
    }

}