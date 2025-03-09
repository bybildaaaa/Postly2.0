package postly.example.postly.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import postly.example.postly.exceptions.ResourceNotFoundException;
import postly.example.postly.models.Comment;
import postly.example.postly.repositories.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
  public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        if (comments.isEmpty()) {
            throw new ResourceNotFoundException("No comments found for post ID: " + postId);
        }
        return comments;
    }
}