package com.laughingenigma.security_service.controller;

import com.laughingenigma.security_service.dto.LoginRequest;
import com.laughingenigma.security_service.dto.LoginResponse;
import com.laughingenigma.security_service.dto.RefreshTokenRequest;
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
                    .body(new LoginResponse("Invalid username or password", null, null));

        }

        String accessToken = this.jwtService.generateAccessToken(user.getUsername(), user.getRole());
        String  refreshToken = this.jwtService.generateRefreshToken(user.getUsername());

        return ResponseEntity
                .ok(new LoginResponse("Login successful", accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            "Invalid or expired refresh token",
                            null,
                            null
                    ));
        }

        String username = jwtService.getUsernameFromToken(refreshToken);

        User user = userService.getUser(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            "Invalid refresh token",
                            null,
                            null
                    ));
        }

        String accessToken = jwtService.generateAccessToken(
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity.ok(
                new LoginResponse(
                        "Access token refreshed",
                        accessToken,
                        null
                )
        );
    }

    @GetMapping("/public-key")
    public ResponseEntity<String> publicKey() {
        String encodedKey = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
        return ResponseEntity.ok(encodedKey);
    }
}
