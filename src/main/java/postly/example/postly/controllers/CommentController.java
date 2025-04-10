package postly.example.postly.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import postly.example.postly.exceptions.InvalidRequestException;
import postly.example.postly.models.Comment;
import postly.example.postly.services.CommentService;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping(params = "postId")
    public List<Comment> getCommentsByPostId(@RequestParam int postId) {
        if (postId <= 0) {
            throw new InvalidRequestException("Invalid post ID");
        }
        return commentService.getCommentsByPostId(postId);
    }

    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(
        @PathVariable int postId, @RequestParam int userId, @RequestParam String text) {
        if (postId <= 0 || userId <= 0 || text.isBlank()) {
            throw new InvalidRequestException("Invalid input data");
        }
        return commentService.addCommentToPost(postId, userId, text);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable int commentId) {
        if (commentId <= 0) {
            throw new InvalidRequestException("Invalid comment ID");
        }
        commentService.deleteComment(commentId);
    }
}