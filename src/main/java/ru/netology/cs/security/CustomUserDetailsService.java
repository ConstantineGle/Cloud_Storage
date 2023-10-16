package ru.netology.cs.security;

import ru.netology.cs.constant.Constant;
import ru.netology.cs.exception.ErrorInput;
import ru.netology.cs.model.user.User;
import ru.netology.cs.repository.UserCrudRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserCrudRepository userCrudRepository;

    public CustomUserDetailsService(UserCrudRepository userCrudRepository) {
        this.userCrudRepository = userCrudRepository;
    }

    @Override
    public User loadUserByUsername(String username) {
        return userCrudRepository.findByLogin(username)
                .orElseThrow(() -> new ErrorInput(Constant.USR_NOT_FOUND));
    }
}
