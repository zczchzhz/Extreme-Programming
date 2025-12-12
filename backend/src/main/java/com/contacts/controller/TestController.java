package com.contacts.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/health")
    public String health() {
        return "Application is running! Database connection status: PENDING";
    }

    @GetMapping("/test")
    public String test() {
        return "Test endpoint works! Basic web framework is functional.";
    }

    @GetMapping("/info")
    public String info() {
        return "Contacts Management System Backend - Test Mode";
    }
}
