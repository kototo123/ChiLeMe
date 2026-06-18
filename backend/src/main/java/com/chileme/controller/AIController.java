package com.chileme.controller;

import com.chileme.common.result.Result;
import com.chileme.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/generate-post")
    public Result<String> generatePost(@RequestBody List<String> imageUrls) {
        return Result.success(aiService.generatePostContent(imageUrls));
    }

    @PostMapping("/audit")
    public Result<String> audit(@RequestParam String text,
                                @RequestBody(required = false) List<String> imageUrls) {
        return Result.success(aiService.auditContent(text, imageUrls));
    }
}
