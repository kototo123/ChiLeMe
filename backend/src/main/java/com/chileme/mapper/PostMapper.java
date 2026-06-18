package com.chileme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chileme.entity.Post;
import org.apache.ibatis.annotations.Update;

public interface PostMapper extends BaseMapper<Post> {

    @Update("UPDATE post SET like_count = like_count + 1 WHERE id = #{postId}")
    void incrementLikeCount(Long postId);

    @Update("UPDATE post SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{postId}")
    void decrementLikeCount(Long postId);

    @Update("UPDATE post SET comment_count = comment_count + 1 WHERE id = #{postId}")
    void incrementCommentCount(Long postId);

    @Update("UPDATE post SET favorite_count = favorite_count + 1 WHERE id = #{postId}")
    void incrementFavoriteCount(Long postId);

    @Update("UPDATE post SET favorite_count = GREATEST(favorite_count - 1, 0) WHERE id = #{postId}")
    void decrementFavoriteCount(Long postId);
}
