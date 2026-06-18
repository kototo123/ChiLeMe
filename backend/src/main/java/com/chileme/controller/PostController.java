package com.chileme.controller;

import com.chileme.common.result.Result;
import com.chileme.dto.PostDTO;
import com.chileme.service.PostService;
import com.chileme.vo.PostVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Result<PostVO> createPost(HttpServletRequest request,
                                     @Valid @RequestBody PostDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(postService.createPost(userId, dto));
    }

    @DeleteMapping("/{postId}")
    public Result<Void> deletePost(HttpServletRequest request,
                                   @PathVariable Long postId) {
        Long userId = (Long) request.getAttribute("userId");
        postService.deletePost(userId, postId);
        return Result.success();
    }

    @GetMapping("/feed")
    public Result<IPage<PostVO>> getFeed(HttpServletRequest request,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(postService.getFeed(page, pageSize, userId));
    }

    @GetMapping("/user/{userId}")
    public Result<IPage<PostVO>> getUserPosts(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int pageSize,
                                              HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        return Result.success(postService.getUserPosts(userId, page, pageSize, currentUserId));
    }

    @GetMapping("/{postId}")
    public Result<PostVO> getPostDetail(@PathVariable Long postId,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(postService.getPostDetail(postId, userId));
    }

    @PostMapping("/{postId}/like")
    public Result<Void> likePost(HttpServletRequest request,
                                 @PathVariable Long postId) {
        Long userId = (Long) request.getAttribute("userId");
        postService.likePost(userId, postId);
        return Result.success();
    }

    @DeleteMapping("/{postId}/like")
    public Result<Void> unlikePost(HttpServletRequest request,
                                   @PathVariable Long postId) {
        Long userId = (Long) request.getAttribute("userId");
        postService.unlikePost(userId, postId);
        return Result.success();
    }

    @PostMapping("/{postId}/favorite")
    public Result<Void> favoritePost(HttpServletRequest request,
                                     @PathVariable Long postId) {
        Long userId = (Long) request.getAttribute("userId");
        postService.favoritePost(userId, postId);
        return Result.success();
    }

    @DeleteMapping("/{postId}/favorite")
    public Result<Void> unfavoritePost(HttpServletRequest request,
                                       @PathVariable Long postId) {
        Long userId = (Long) request.getAttribute("userId");
        postService.unfavoritePost(userId, postId);
        return Result.success();
    }
}
