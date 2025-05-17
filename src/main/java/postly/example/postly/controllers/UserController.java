package postly.example.postly.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org. springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import postly.example.postly.exceptions.InvalidRequestException;
import postly.example.postly.models.User;
import postly.example.postly.services.UserService;

@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Операции с пользователями")
public class UserController {

    private final UserService userService;

    @Autowired
  public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей",
        description = "Возвращает список всех зарегистрированных пользователей")
  public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получить пользователя по ID",
        description = "Возвращает пользователя по его уникальному идентификатору")
  public User getUserById(@PathVariable int userId) {
        if (userId <= 0) {
            throw new InvalidRequestException("Invalid user ID");
        }
        return userService.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать нового пользователя",
        description = "Создаёт нового пользователя с указанным именем")
    public User createUser(@RequestParam String username) {
        if (username.isBlank()) {
            throw new InvalidRequestException("Username cannot be empty");
        }
        return userService.createUser(username);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по его ID")
    public void deleteUser(@PathVariable int userId) {
        if (userId <= 0) {
            throw new InvalidRequestException("Invalid user ID");
        }
        userService.deleteUser(userId);
    }

    @PatchMapping("/{userId}/{newUsername}")
    @Operation(summary = "Обновить имя пользователя",
        description = "Обновляет имя пользователя по ID на новое значение")
    public ResponseEntity<User> updateUsername(@PathVariable int userId,
                                               @PathVariable String newUsername) {
        if (userId <= 0 || newUsername.isBlank()) {
            throw new InvalidRequestException("Invalid input data");
        }
        User updatedUser = userService.updateUsername(userId, newUsername);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/login")
    @Operation(summary = "Вход по имени пользователя",
        description = "Возвращает пользователя по имени, если он существует")
  public ResponseEntity<User> login(@RequestParam String username) {
        if (username.isBlank()) {
            throw new InvalidRequestException("Username cannot be empty");
        }
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new InvalidRequestException("User not found");
        }
        return ResponseEntity.ok(user);
    }
}
