package postly.example.postly.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import postly.example.postly.services.PostService;
import postly.example.postly.models.Post;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService; // исправленное название переменной

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable int id) {
        return postService.getPostById(id);
    }

    @GetMapping(params = "username")
    public List<Post> getPostsByUsername(@RequestParam String username) {
        return postService.getPostsByUsername(username);
    }
}
