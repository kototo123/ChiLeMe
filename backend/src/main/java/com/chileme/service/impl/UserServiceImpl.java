package com.chileme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chileme.common.exception.BusinessException;
import com.chileme.common.utils.JwtUtils;
import com.chileme.dto.UserLoginDTO;
import com.chileme.entity.User;
import com.chileme.mapper.UserMapper;
import com.chileme.service.UserService;
import com.chileme.vo.LoginVO;
import com.chileme.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    @Override
    public LoginVO wxLogin(UserLoginDTO dto) {
        String openid = mockWxLogin(dto.getCode());
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname(dto.getNickname() != null ? dto.getNickname() : "微信用户");
            user.setAvatar(dto.getAvatar() != null ? dto.getAvatar() : "");
            user.setGender(dto.getGender() != null ? dto.getGender() : 0);
            user.setTotalScore(0);
            user.setContinuousDays(0);
            user.setMaxContinuousDays(0);
            user.setCurrentMonthOnTime(0);
            user.setCurrentMonthScore(0);
            user.setBreakCardCount(2);
            user.setMonthlyBreakUsed(0);
            user.setStatus(1);
            userMapper.insert(user);
        }
        String token = jwtUtils.generateToken(user.getId(), user.getOpenid());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUser(toUserVO(user));
        return vo;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        return toUserVO(user);
    }

    @Override
    public void updateProfile(Long userId, String nickname, String avatar, Integer gender) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setNickname(nickname);
        user.setAvatar(avatar);
        user.setGender(gender);
        userMapper.updateById(user);
    }

    private String mockWxLogin(String code) {
        return "mock_openid_" + code.hashCode();
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
