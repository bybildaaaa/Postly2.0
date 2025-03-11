package postly.example.postly.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import postly.example.postly.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
