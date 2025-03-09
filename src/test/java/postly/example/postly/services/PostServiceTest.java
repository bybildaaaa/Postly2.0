package postly.example.postly.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import postly.example.postly.models.Post;

import java.util.List;

@SpringBootTest
class PostServiceTest {

  @Autowired
  private PostService postService;

  @Test
  void testGetAllPosts() {
    List<Post> posts = postService.getAllPosts();
    assertFalse(posts.isEmpty());
  }

  @Test
  void testGetPostById() {
    Post post = postService.getPostById(1);
    assertEquals("Biba", post.getUsername());
  }
}