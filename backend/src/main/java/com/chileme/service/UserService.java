package com.chileme.service;

import com.chileme.dto.UserLoginDTO;
import com.chileme.vo.LoginVO;
import com.chileme.vo.UserVO;

public interface UserService {
    LoginVO wxLogin(UserLoginDTO dto);
    UserVO getUserInfo(Long userId);
    void updateProfile(Long userId, String nickname, String avatar, Integer gender);
}
