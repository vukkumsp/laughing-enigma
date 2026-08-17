package com.laughingenigma.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test/protected")
    public String protectedEndpoint() {
        return "You reached a protected endpoint";
    }
}
