package com.chileme.controller;

import com.chileme.common.result.Result;
import com.chileme.dto.CommentDTO;
import com.chileme.entity.Comment;
import com.chileme.service.CommentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
                                      @Valid @RequestBody CommentDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(commentService.addComment(userId, postId, dto.getParentId(), dto.getReplyTo(), dto.getContent()));
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
