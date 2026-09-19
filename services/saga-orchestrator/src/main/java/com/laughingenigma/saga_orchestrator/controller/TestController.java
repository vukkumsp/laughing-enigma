package com.laughingenigma.saga_orchestrator.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Profile("dev")
public class TestController {

    @GetMapping("test")
    String test() {
        return "Test Successful";
    }
}
