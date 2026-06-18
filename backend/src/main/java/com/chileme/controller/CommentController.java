package com.chileme.controller;

import com.chileme.common.result.Result;
import com.chileme.entity.Comment;
import com.chileme.service.CommentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public Result<Comment> addComment(HttpServletRequest request,
                                      @PathVariable Long postId,
                                      @RequestParam(required = false) Long parentId,
                                      @RequestParam(required = false) Long replyTo,
                                      @NotBlank @RequestParam String content) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(commentService.addComment(userId, postId, parentId, replyTo, content));
    }

    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(HttpServletRequest request,
                                      @PathVariable Long commentId) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.deleteComment(userId, commentId);
        return Result.success();
    }

    @GetMapping("/{postId}")
    public Result<IPage<Comment>> getComments(@PathVariable Long postId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int pageSize) {
        return Result.success(commentService.getComments(postId, page, pageSize));
    }
}
