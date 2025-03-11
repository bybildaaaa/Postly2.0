package postly.example.postly.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import postly.example.postly.exceptions.ResourceNotFoundException;
import postly.example.postly.models.Comment;
import postly.example.postly.models.Post;
import postly.example.postly.models.User;
import postly.example.postly.repositories.CommentRepository;
import postly.example.postly.repositories.PostRepository;
import postly.example.postly.repositories.UserRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
  public CommentService(
        CommentRepository commentRepository,
        PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        if (comments.isEmpty()) {
            throw new ResourceNotFoundException("No comments found for post ID: " + postId);
        }
        return comments;
    }

    public Comment addCommentToPost(int postId, String username, String text) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found: " + username);
        }

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment comment = new Comment(username, text, post);
        return commentRepository.save(comment);
    }

    public void deleteComment(int commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }
}