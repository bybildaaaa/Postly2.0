package postly.example.postly.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import postly.example.postly.models.User;
import postly.example.postly.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
  public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
  public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
  public User getUserById(@PathVariable int userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
  public User createUser(@RequestParam String username) {
        return userService.createUser(username);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
    }

    @PatchMapping("/{userId}/{newUsername}")
  public ResponseEntity<User> updateUsername(
        @PathVariable int userId,
        @PathVariable String newUsername) {

        User updatedUser = userService.updateUsername(userId, newUsername);
        return ResponseEntity.ok(updatedUser);
    }

}
