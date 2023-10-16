package ru.netology.cs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cs.model.user.User;
import java.util.Optional;

@Repository
public interface UserCrudRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String username);
}
