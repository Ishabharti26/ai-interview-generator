package com.isha.aiinterview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /** Serve the SPA at root and any non-API path */
    @GetMapping(value = {"/"})
    public String home() {
        return "forward:/index.html";
    }
}
