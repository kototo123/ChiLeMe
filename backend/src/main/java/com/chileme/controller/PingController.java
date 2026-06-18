package com.chileme.controller;

import com.chileme.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("pong");
    }
}
