package com.laughingenigma.security_service.controller;

import com.laughingenigma.security_service.dto.LoginRequest;
import com.laughingenigma.security_service.dto.LoginResponse;
import com.laughingenigma.security_service.dto.RegisterRequest;
import com.laughingenigma.security_service.entity.User;
import com.laughingenigma.security_service.service.JwtService;
import com.laughingenigma.security_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.Base64;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final PublicKey publicKey;

    @Autowired
    public AuthController(UserService userService, JwtService jwtService,  PublicKey publicKey) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.publicKey = publicKey;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request){
        this.userService.createUser(request.getUsername(), request.getPassword());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        User user = this.userService.authenticate(request.getUsername(), request.getPassword());

        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Invalid username or password", null));

        }

        String token = this.jwtService.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity
                .ok(new LoginResponse("Login successful", token));
    }

    @GetMapping("/public-key")
    public ResponseEntity<String> publicKey() {
        String encodedKey = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
        return ResponseEntity.ok(encodedKey);
    }
}
