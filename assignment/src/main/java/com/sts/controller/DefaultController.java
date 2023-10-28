package com.sts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String redirectToLoginPage() {
        return "redirect:/app/login";
    }
}
