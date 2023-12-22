package ita.univey.domain.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/health")
    public String healthCheck() {
        return "I'm healthy!!!";
    }
}
