package com.ahuralearn.featuretest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@Slf4j
public class testController {

    @GetMapping("/1")
    public String test0() {
        log.info("Successful access");
        return "abcd";
    }

    @PostMapping
    public String test2() {
        log.info("Successful access");
        return "abcd";
    }

    @PutMapping
    public String test3() {
        log.info("Successful access");
        return "abcd";
    }

    @DeleteMapping
    public String test4() {
        log.info("Successful access");
        return "abcd";
    }
}
