package com.chileme.controller;

import com.chileme.common.result.Result;
import com.chileme.entity.Follow;
import com.chileme.mapper.FollowMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowMapper followMapper;

    @PostMapping("/{userId}")
    public Result<Void> follow(HttpServletRequest request, @PathVariable Long userId) {
        Long followerId = (Long) request.getAttribute("userId");
        if (followerId.equals(userId)) {
            return Result.error("不能关注自己");
        }
        if (followMapper.isFollowing(followerId, userId)) {
            return Result.error("已关注");
        }
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFolloweeId(userId);
        followMapper.insert(follow);
        return Result.success();
    }

    @DeleteMapping("/{userId}")
    public Result<Void> unfollow(HttpServletRequest request, @PathVariable Long userId) {
        Long followerId = (Long) request.getAttribute("userId");
        followMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Follow>()
                        .eq(Follow::getFollowerId, followerId)
                        .eq(Follow::getFolloweeId, userId));
        return Result.success();
    }
}
