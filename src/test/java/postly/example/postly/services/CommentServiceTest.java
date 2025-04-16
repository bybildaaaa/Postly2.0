package postly.example.postly.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.List;
import postly.example.postly.models.Comment;
import postly.example.postly.models.Post;
import postly.example.postly.models.User;
import postly.example.postly.repositories.CommentRepository;
import postly.example.postly.repositories.PostRepository;
import postly.example.postly.repositories.UserRepository;
import postly.example.postly.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private PostRepository postRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CommentService commentService;

  private User user;
  private Post post;
  private Comment comment;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1);
    user.setUsername("testuser");

    post = new Post();
    post.setId(1);
    post.setUsername("testuser");
    post.setPost("Test Post");

    comment = new Comment("testuser", "Test Comment", post);
    comment.setId(1);
  }

  @Test
  void testGetCommentsByPostId_Success() {
    when(commentRepository.findByPostId(1)).thenReturn(Arrays.asList(comment));

    List<Comment> comments = commentService.getCommentsByPostId(1);

    assertNotNull(comments);
    assertEquals(1, comments.size());
    assertEquals("Test Comment", comments.get(0).getText());
    verify(commentRepository, times(1)).findByPostId(1);
  }

  @Test
  void testGetCommentsByPostId_NoCommentsFound() {
    when(commentRepository.findByPostId(1)).thenReturn(Collections.emptyList());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      commentService.getCommentsByPostId(1);
    });

    assertEquals("No comments found for post ID: 1", exception.getMessage());
    verify(commentRepository, times(1)).findByPostId(1);
  }

  @Test
  void testAddCommentToPost_Success() {
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(postRepository.findById(1)).thenReturn(Optional.of(post));
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    Comment savedComment = commentService.addCommentToPost(1, 1, "Test Comment");

    assertNotNull(savedComment);
    assertEquals("testuser", savedComment.getUsername());
    assertEquals("Test Comment", savedComment.getText());
    verify(userRepository, times(1)).findById(1);
    verify(postRepository, times(1)).findById(1);
    verify(commentRepository, times(1)).save(any(Comment.class));
  }

  @Test
  void testAddCommentToPost_UserNotFound() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      commentService.addCommentToPost(1, 1, "Test Comment");
    });

    assertEquals("User not found", exception.getMessage());
    verify(userRepository, times(1)).findById(1);
    verify(postRepository, never()).findById(anyInt());
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  void testAddCommentToPost_PostNotFound() {
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(postRepository.findById(1)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      commentService.addCommentToPost(1, 1, "Test Comment");
    });

    assertEquals("Post not found", exception.getMessage());
    verify(userRepository, times(1)).findById(1);
    verify(postRepository, times(1)).findById(1);
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  void testDeleteComment_Success() {
    when(commentRepository.existsById(1)).thenReturn(true);
    doNothing().when(commentRepository).deleteById(1);

    assertDoesNotThrow(() -> commentService.deleteComment(1));

    verify(commentRepository, times(1)).existsById(1);
    verify(commentRepository, times(1)).deleteById(1);
  }

  @Test
  void testDeleteComment_CommentNotFound() {
    when(commentRepository.existsById(1)).thenReturn(false);

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      commentService.deleteComment(1);
    });

    assertEquals("Comment not found", exception.getMessage());
    verify(commentRepository, times(1)).existsById(1);
    verify(commentRepository, never()).deleteById(anyInt());
  }
}
