package com.laughingenigma.security_service.controller;

import com.laughingenigma.security_service.dto.LoginRequest;
import com.laughingenigma.security_service.dto.LoginResponse;
import com.laughingenigma.security_service.dto.RegisterRequest;
import com.laughingenigma.security_service.entity.User;
import com.laughingenigma.security_service.service.JwtService;
import com.laughingenigma.security_service.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";
    private static final String COOKIE_PATH = "/api/v1/auth";

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

        String accessToken = this.jwtService.generateAccessToken(user.getUsername(), user.getRole());
        String  refreshToken = this.jwtService.generateRefreshToken(user.getUsername());

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
//                For Production use secure true & sameSite None
//                TODO: make this config dynamic based on deployment
//                .secure(true)
//                .sameSite("None")
                .secure(false)
                .sameSite("Lax")
                .path(COOKIE_PATH)
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshTokenCookie.toString()
                )
                .body(new LoginResponse(
                        "Login successful",
                        accessToken
                ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(
                    name = REFRESH_TOKEN_COOKIE,
                    required = false
            ) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            "Refresh token is missing",
                            null
                    ));
        }

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            "Invalid or expired refresh token",
                            null
                    ));
        }

        String username = jwtService.getUsernameFromToken(refreshToken);

        User user = userService.getUser(username);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            "Invalid refresh token",
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
                        accessToken
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout() {

        /*
         * Later, when refresh-token persistence/revocation is added,
         * revoke the refresh token here before clearing the cookie.
         */

        ResponseCookie deleteCookie = ResponseCookie
                .from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
//                For Production use secure true & sameSite None
//                TODO: make this config dynamic based on deployment
//                .secure(true)
//                .sameSite("None")
                .path(COOKIE_PATH)
                .maxAge(0)
                .build();

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        deleteCookie.toString()
                )
                .body(new LoginResponse(
                        "Logout successful",
                        null
                ));
    }

    @GetMapping("/public-key")
    public ResponseEntity<String> publicKey() {
        String encodedKey = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
        return ResponseEntity.ok(encodedKey);
    }
}
