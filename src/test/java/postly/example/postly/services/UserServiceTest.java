package postly.example.postly.services;

import postly.example.postly.cashe.CacheService;
import postly.example.postly.exceptions.ResourceNotFoundException;
import postly.example.postly.exceptions.UserAlreadyExistsException;
import postly.example.postly.models.Comment;
import postly.example.postly.models.Post;
import postly.example.postly.models.User;
import postly.example.postly.repositories.CommentRepository;
import postly.example.postly.repositories.PostRepository;
import postly.example.postly.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PostRepository postRepository;
  @Mock
  private CommentRepository commentRepository;
  @Mock
  private CacheService<Integer, Post> cacheService;

  @InjectMocks
  private UserService userService;

  private User user;
  private Post post;
  private Comment comment;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1);
    user.setUsername("user1");

    post = new Post();
    post.setId(10);
    post.setUsername("user1");
    post.setLikedByUsers(List.of(user));  // Используем List вместо HashSet

    comment = new Comment("user1", "text", post);
  }

  @Test
  void getAllUsers() {
    when(userRepository.findAll()).thenReturn(List.of(user));
    List<User> users = userService.getAllUsers();
    assertEquals(1, users.size());
    verify(userRepository).findAll();
  }

  @Test
  void getUserByIdSuccess() {
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    User result = userService.getUserById(1);
    assertEquals("user1", result.getUsername());
  }

  @Test
  void getUserByIdNotFound() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1));
  }

  @Test
  void createUserSuccess() {
    when(userRepository.findByUsername("user2")).thenReturn(null);
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    User created = userService.createUser("user2");

    assertEquals("user2", created.getUsername());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void createUserAlreadyExists() {
    when(userRepository.findByUsername("user1")).thenReturn(user);
    assertThrows(UserAlreadyExistsException.class, () -> userService.createUser("user1"));
  }

  /*@Test
  void deleteUserSuccess() {
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(postRepository.findAll()).thenReturn(List.of(post));
    when(postRepository.findByUsername("user1")).thenReturn(List.of(post));
    when(commentRepository.findAll()).thenReturn(List.of(comment));

    userService.deleteUser(1);

    verify(postRepository).save(post);
    verify(postRepository).deleteAll(List.of(post));
    verify(commentRepository).deleteAll(anyList());
    verify(userRepository).delete(user);
  }*/

  @Test
  void deleteUserNotFound() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1));
  }

  @Test
  void updateUsernameSuccess() {
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(postRepository.findPostsByUsername("user1")).thenReturn(List.of(post));
    when(commentRepository.findAll()).thenReturn(List.of(comment));
    when(cacheService.get(10)).thenReturn(post);

    User updated = userService.updateUsername(1, "newUser");

    assertEquals("newUser", updated.getUsername());
    verify(userRepository).save(user);
    verify(postRepository).save(post);
    verify(commentRepository).save(comment);
    verify(cacheService).put(10, post);
  }

  @Test
  void updateUsernameUserNotFound() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.updateUsername(1, "any"));
  }

  @Test
  void updateUsernameWithEmptyCache() {
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(postRepository.findPostsByUsername("user1")).thenReturn(List.of(post));
    when(commentRepository.findAll()).thenReturn(List.of(comment));
    when(cacheService.get(10)).thenReturn(null); // ничего не кэшировано

    User updated = userService.updateUsername(1, "newUser");
    verify(cacheService, never()).put(anyInt(), any());
  }
}
