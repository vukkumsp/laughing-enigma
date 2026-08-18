package com.laughingenigma.security_service.service;


import com.laughingenigma.security_service.entity.Role;
import com.laughingenigma.security_service.entity.User;
import com.laughingenigma.security_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public User authenticate(String username, String rawPassword) {

        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(
                rawPassword,
                user.getPassword())
        ) {
            return null;
        }
        return user;
    }
}
