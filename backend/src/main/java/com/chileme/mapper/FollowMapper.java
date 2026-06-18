package com.chileme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chileme.entity.Follow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface FollowMapper extends BaseMapper<Follow> {

    @Select("SELECT COUNT(*) > 0 FROM follow WHERE follower_id = #{followerId} AND followee_id = #{followeeId}")
    boolean isFollowing(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);
}
