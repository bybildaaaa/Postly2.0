package postly.example.postly.services;

import postly.example.postly.cashe.CacheService;
import postly.example.postly.exceptions.InvalidRequestException;
import postly.example.postly.exceptions.ResourceNotFoundException;
import postly.example.postly.models.Post;
import postly.example.postly.repositories.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import postly.example.postly.repositories.UserRepository;
import postly.example.postly.models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CacheService<Integer, Post> cacheService;

  @InjectMocks
  private PostService postService;

  @Test
  void testCreatePost() {
    int userId = 1;
    String text = "Test Content";
    User user = new User();
    user.setId(userId);
    user.setUsername("TestUser");

    Post post = new Post();
    post.setId(1);
    post.setUsername(user.getUsername());
    post.setPost(text);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(postRepository.save(any(Post.class))).thenReturn(post);

    Post createdPost = postService.createPost(userId, text);

    assertNotNull(createdPost);
    assertEquals("TestUser", createdPost.getUsername());
    assertEquals(text, createdPost.getPost());
    verify(cacheService).put(post.getId(), post);
  }

  @Test
  void testGetPostById_CacheHit() {
    int postId = 1;
    Post cachedPost = new Post();
    cachedPost.setId(postId);
    cachedPost.setUsername("TestUser");
    cachedPost.setPost("Cached Content");

    when(cacheService.get(postId)).thenReturn(cachedPost);

    Post post = postService.getPostById(postId);

    assertNotNull(post);
    assertEquals("TestUser", post.getUsername());
    verify(postRepository, never()).findById(anyInt());
  }

  @Test
  void testGetPostById_CacheMiss() {
    int postId = 1;
    Post dbPost = new Post();
    dbPost.setId(postId);
    dbPost.setUsername("TestUser");
    dbPost.setPost("DB Content");

    when(cacheService.get(postId)).thenReturn(null);
    when(postRepository.findById(postId)).thenReturn(Optional.of(dbPost));

    Post post = postService.getPostById(postId);

    assertNotNull(post);
    assertEquals("TestUser", post.getUsername());
    verify(cacheService).put(postId, dbPost);
  }

  @Test
  void testDeletePost_ExistingPost() {
    when(postRepository.existsById(1)).thenReturn(true);
    doNothing().when(postRepository).deleteById(1);

    postService.deletePost(1);

    verify(postRepository).existsById(1);
    verify(postRepository).deleteById(1);
    verify(cacheService).remove(1);
  }

  @Test
  void testDeletePost_NonExistingPost() {
    when(postRepository.existsById(1)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(1));
    verify(postRepository).existsById(1);
    verify(postRepository, never()).deleteById(anyInt());
    verify(cacheService, never()).remove(anyInt());
  }

  @Test
  void testLikePost() {
    int postId = 1;
    int userId = 2;
    Post post = new Post();
    post.setId(postId);
    post.setUsername("Author");
    post.setPost("Content");
    post.setLikedByUsers(new ArrayList<>());

    User user = new User();
    user.setId(userId);
    user.setUsername("Liker");

    when(cacheService.get(postId)).thenReturn(null);
    when(postRepository.findById(postId)).thenReturn(Optional.of(post));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(postRepository.save(post)).thenReturn(post);

    postService.likePost(postId, userId);

    assertTrue(post.getLikedByUsers().contains(user));
    verify(cacheService, times(2)).put(eq(1), any(Post.class));
  }

  @Test
  void testUnlikePost() {
    int postId = 1;
    int userId = 2;
    User user = new User();
    user.setId(userId);
    user.setUsername("Unliker");

    Post post = new Post();
    post.setId(postId);
    post.setUsername("Author");
    post.setPost("Content");
    post.setLikedByUsers(new ArrayList<>(List.of(user)));

    when(cacheService.get(postId)).thenReturn(null);
    when(postRepository.findById(postId)).thenReturn(Optional.of(post));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(postRepository.save(post)).thenReturn(post);

    postService.unlikePost(postId, userId);

    assertFalse(post.getLikedByUsers().contains(user));
    verify(cacheService, times(2)).put(eq(1), any(Post.class));
  }

  @Test
  void testGetUsersWhoLikedPost() {
    int postId = 1;
    User user1 = new User();
    user1.setId(1);
    user1.setUsername("User1");

    User user2 = new User();
    user2.setId(2);
    user2.setUsername("User2");

    Post post = new Post();
    post.setId(postId);
    post.setLikedByUsers(List.of(user1, user2));

    when(postRepository.findById(postId)).thenReturn(Optional.of(post));

    List<User> likedUsers = postService.getUsersWhoLikedPost(postId);

    assertEquals(2, likedUsers.size());
    assertTrue(likedUsers.contains(user1));
    assertTrue(likedUsers.contains(user2));
  }

  @Test
  void testGetPostsByUserId() {
    int userId = 1;
    User user = new User();
    user.setId(userId);
    user.setUsername("TestUser");

    List<Post> posts = List.of(new Post(), new Post());

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(postRepository.findByUsername("TestUser")).thenReturn(posts);

    List<Post> result = postService.getPostsByUserId(userId);

    assertEquals(2, result.size());
  }

  @Test
  void testGetLikesCount_ExistingPost() {
    when(postRepository.existsById(1)).thenReturn(true);
    when(postRepository.getLikesCount(1)).thenReturn(5);

    int likes = postService.getLikesCount(1);

    assertEquals(5, likes);
    verify(postRepository).existsById(1);
    verify(postRepository).getLikesCount(1);
  }

  @Test
  void testGetLikesCount_NonExistingPost() {
    when(postRepository.existsById(1)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> postService.getLikesCount(1));
    verify(postRepository).existsById(1);
    verify(postRepository, never()).getLikesCount(anyInt());
  }

  @Test
  void testSavePost() {
    Post post = new Post();
    post.setPost("Sample post");
    when(postRepository.save(post)).thenReturn(post);

    Post savedPost = postService.save(post);

    assertEquals("Sample post", savedPost.getPost());
    verify(postRepository).save(post);
  }

  @Test
  void testGetPostsByUsername() {
    List<Post> posts = List.of(new Post(), new Post());
    when(postRepository.findPostsByUsername("user1")).thenReturn(posts);

    List<Post> result = postService.getPostsByUsername("user1");

    assertEquals(2, result.size());
    verify(postRepository).findPostsByUsername("user1");
  }

  @Test
  void testGetPostsByMinLikes() {
    List<Post> posts = List.of(new Post(), new Post(), new Post());
    when(postRepository.findPostsByMinLikes(10)).thenReturn(posts);

    List<Post> result = postService.getPostsByMinLikes(10);

    assertEquals(3, result.size());
    verify(postRepository).findPostsByMinLikes(10);
  }

  @Test
  void testGetAllPosts() {
    List<Post> posts = List.of(new Post(), new Post());
    when(postRepository.findAll()).thenReturn(posts);

    List<Post> result = postService.getAllPosts();

    assertEquals(2, result.size());
    verify(postRepository).findAll();
  }

  @Test
  void testCreatePostsBulk_Success() {
    int userId = 1;
    User user = new User();
    user.setId(userId);
    user.setUsername("BulkUser");

    List<String> texts = List.of("Post 1", "Post 2", "  ", "", "Post 3");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    List<Post> savedPosts = texts.stream()
      .filter(text -> !text.isBlank())
      .map(text -> {
        Post post = new Post();
        post.setId(text.hashCode()); // просто для имитации ID
        post.setUsername(user.getUsername());
        post.setPost(text);
        return post;
      })
      .collect(Collectors.toList());

    when(postRepository.saveAll(anyList())).thenReturn(savedPosts);

    List<Post> result = postService.createPostsBulk(userId, texts);

    assertEquals(3, result.size()); // только непустые
    for (Post post : result) {
      assertEquals("BulkUser", post.getUsername());
      assertNotNull(post.getPost());
      verify(cacheService).put(post.getId(), post);
    }

    verify(postRepository).saveAll(anyList());
  }

  @Test
  void testCreatePostsBulk_EmptyTextList() {
    int userId = 1;
    User user = new User();
    user.setId(userId);
    user.setUsername("BulkUser");

    List<String> texts = List.of("", " ", "   "); // все пустые/пробелы

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(postRepository.saveAll(anyList())).thenReturn(List.of());

    List<Post> result = postService.createPostsBulk(userId, texts);

    assertTrue(result.isEmpty());
    verify(postRepository).saveAll(List.of());
    verifyNoInteractions(cacheService);
  }

  @Test
  void testCreatePostsBulk_UserNotFound() {
    int userId = 99;
    List<String> texts = List.of("Test Post");

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(InvalidRequestException.class, () -> postService.createPostsBulk(userId, texts));
    verify(userRepository).findById(userId);
    verifyNoInteractions(postRepository);
    verifyNoInteractions(cacheService);
  }

}
