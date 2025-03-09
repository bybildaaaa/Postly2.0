package postly.example.postly.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import postly.example.postly.models.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUsername(String username);
}
