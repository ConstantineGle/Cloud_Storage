package ru.netology.cs.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cs.constant.Constant;
import ru.netology.cs.exception.ErrorInput;
import ru.netology.cs.service.AuthorizationService;
import java.util.Collections;
import java.util.Map;

@CrossOrigin(origins = {"${settings.cors_origin}"}, allowedHeaders = "*", allowCredentials = "true")
@RestController
public class AuthorizationController {
    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping("/login")
    public Map<String, String> loginUser(@RequestBody Map<String, String> auth) {
        String token = authorizationService.login(auth.get("login"), auth.get("password"));
        return Collections.singletonMap("auth-token", token);
    }

    @PostMapping("/logout")
    public String logoutUser() {
        return Constant.SUCCESS_LOGOUT;
    }

    @GetMapping("/login")
    public String logout() {
        return Constant.SUCCESS_LOGOUT;
    }

    @ExceptionHandler(ErrorInput.class)
    ResponseEntity<String> handlerErrorInputData(ErrorInput errorInput) {
        return new ResponseEntity<>(authorizationService.makeErrorJson(errorInput.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<String> handlerRuntimeException(RuntimeException runtimeException) {
        runtimeException.printStackTrace();
        return new ResponseEntity<>(authorizationService.makeErrorJson(Constant.SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}