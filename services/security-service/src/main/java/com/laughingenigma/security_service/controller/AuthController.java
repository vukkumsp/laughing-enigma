package com.laughingenigma.security_service.controller;

import com.laughingenigma.security_service.dto.RegisterRequest;
import com.laughingenigma.security_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request){
        this.userService.createUser(request.getUsername(), request.getPassword());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
