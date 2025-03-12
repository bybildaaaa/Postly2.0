package postly.example.postly.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import postly.example.postly.models.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUsername(String username);

    @Query("SELECT COUNT(pl) FROM Post p JOIN p.likedByUsers pl WHERE p.id = :postId")
    int getLikesCount(@Param("postId") int postId);
}
