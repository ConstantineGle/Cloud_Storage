package ru.netology.cs.service;

import org.springframework.stereotype.Service;
import ru.netology.cs.constant.Constant;
import ru.netology.cs.exception.ErrorInput;
import ru.netology.cs.repository.CloudRepository;

@Service
public class AuthorizationService {
    private final CloudRepository cloudRepository;

    public AuthorizationService(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    /** login **/
    public String login(String login, String password) {
        if (login == null || login.isEmpty() || password == null || password.isEmpty()) throw new ErrorInput(Constant.EMPTY_LOG_PASS);
        return cloudRepository.login(login, password);
    }

    public String makeErrorJson(String msg) { return cloudRepository.makeErrorJson(msg);}
}
