package postly.example.postly.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io. swagger. v3.oas. annotations. Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org. springframework. web. bind. annotation. ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org. springframework. web. bind. annotation. RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import postly.example.postly.exceptions.InvalidRequestException;
import postly.example.postly.exceptions.ResourceNotFoundException;
import postly.example.postly.models.Post;
import postly.example.postly.models.User;
import postly.example.postly.services.PostService;

@RestController
@RequestMapping("/posts")
@Tag(name = "Посты", description = "Операции с постами")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{postId}/likes/count")
    @Operation(summary = "Получить количество лайков поста",
          description = "Возвращает общее количество лайков для указанного поста по его ID")
    public int getLikesCount(@PathVariable int postId) {
        if (postId <= 0) {
            throw new InvalidRequestException("Invalid post ID");
        }
        return postService.getLikesCount(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новый пост",
          description = "Создаёт новый пост от имени пользователя с заданным текстом")
    public Post createPost(@RequestParam int userId, @RequestParam String text) {
        if (userId <= 0 || text.isBlank()) {
            throw new InvalidRequestException("Invalid input data");
        }
        return postService.createPost(userId, text);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить пост", description = "Удаляет пост по его ID")
    public void deletePost(@PathVariable int postId) {
        postService.deletePost(postId);
    }

    @GetMapping
    @Operation(summary = "Получить все посты", description = "Возвращает список всех постов")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пост по ID",
          description = "Возвращает пост по его уникальному идентификатору")
    public Post getPostById(@PathVariable String id) {
        try {
            int postId = Integer.parseInt(id); // Преобразуем id в число
            if (postId <= 0) {
                throw new InvalidRequestException("Invalid post ID: " + id);
            }
            return postService.getPostById(postId);
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Invalid post ID format: " + id);
        }
    }

    @PostMapping("/{postId}/like")
    @Operation(summary = "Поставить лайк посту",
          description = "Добавляет лайк к посту от указанного пользователя")
    public ResponseEntity<String> likePost(@PathVariable int postId, @RequestParam int userId) {
        if (postId <= 0 || userId <= 0) {
            throw new InvalidRequestException("Invalid input data");
        }
        postService.likePost(postId, userId);
        return ResponseEntity.ok("Post liked successfully");
    }

    @DeleteMapping("/{postId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Убрать лайк с поста",
          description = "Удаляет лайк от пользователя с указанного поста")
    public void unlikePost(@PathVariable int postId, @RequestParam int userId) {
        postService.unlikePost(postId, userId);
    }

    @GetMapping("/{postId}/likes")
    @Operation(summary = "Получить пользователей, лайкнувших пост",
          description = "Возвращает список пользователей, которые поставили лайк указанному посту")
    public List<User> getUsersWhoLikedPost(@PathVariable int postId) {
        return postService.getUsersWhoLikedPost(postId);
    }

    @GetMapping(params = "userId")
    @Operation(summary = "Получить посты пользователя по ID",
          description = "Возвращает список постов, созданных пользователем с указанным ID")
    public List<Post> getPostsByUserId(@RequestParam int userId) {
        return postService.getPostsByUserId(userId);
    }

    @GetMapping("/filter/username")
    @Operation(summary = "Получить посты по имени пользователя",
          description = "Возвращает список постов, созданных пользователем с указанным именем")
    public List<Post> getPostsByUsername(@RequestParam String username) {
        return postService.getPostsByUsername(username);
    }

    @GetMapping("/filter/min-likes")
    @Operation(summary = "Получить посты с минимальным количеством лайков",
          description = "Возвращает список постов, "
            + "у которых количество лайков больше или равно указанному значению")
    public List<Post> getPostsByMinLikes(@RequestParam int likesCount) {
        return postService.getPostsByMinLikes(likesCount);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ex.getMessage();
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать несколько постов",
        description = "Создаёт несколько новых постов от имени пользователя с заданными текстами")
    public List<Post> createPostsBulk(
        @Parameter(description = "ID пользователя", required = true)
        @RequestParam int userId,

        @Parameter(description = "Список текстов для постов", required = true)
        @RequestBody List<String> texts) {

        if (userId <= 0 || texts == null || texts.isEmpty()) {
            throw new InvalidRequestException("Invalid input data");
        }

        return postService.createPostsBulk(userId, texts);
    }

}
