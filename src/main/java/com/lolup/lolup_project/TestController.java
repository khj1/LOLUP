package com.lolup.lolup_project;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${front.redirect_url}")
    private String url;

    @GetMapping("/message")
    public String getMessage() {
        return url;
    }
}
