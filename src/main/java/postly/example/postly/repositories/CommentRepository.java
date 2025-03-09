package postly.example.postly.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import postly.example.postly.models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostId(int postId);
}
