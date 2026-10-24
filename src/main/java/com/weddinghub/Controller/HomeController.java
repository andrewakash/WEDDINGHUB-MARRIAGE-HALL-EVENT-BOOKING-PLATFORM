package com.weddinghub.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {
    @GetMapping("/api/v1/home")
    public Map<String,String> home(){
        return Map.of(
                "message", "Welcome to WeddingHub!",
                "status", "SUCCESS"
        );
    }
}
