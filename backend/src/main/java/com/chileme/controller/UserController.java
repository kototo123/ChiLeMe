package com.chileme.controller;

import com.chileme.common.result.Result;
import com.chileme.dto.UserLoginDTO;
import com.chileme.service.UserService;
import com.chileme.vo.LoginVO;
import com.chileme.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("ok");
    }

    @PostMapping("/wx-login")
    public Result<LoginVO> wxLogin(@Valid @RequestBody UserLoginDTO dto) {
        return Result.success(userService.wxLogin(dto));
    }

    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getUserInfo(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(HttpServletRequest request,
                                      @RequestParam String nickname,
                                      @RequestParam(required = false) String avatar,
                                      @RequestParam(required = false) Integer gender) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, nickname, avatar, gender);
        return Result.success();
    }
}
