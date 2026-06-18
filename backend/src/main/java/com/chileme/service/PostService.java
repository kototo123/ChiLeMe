package com.chileme.service;

import com.chileme.dto.PostDTO;
import com.chileme.vo.PostVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface PostService {
    PostVO createPost(Long userId, PostDTO dto);
    void deletePost(Long userId, Long postId);
    IPage<PostVO> getFeed(int page, int pageSize, Long currentUserId);
    IPage<PostVO> getUserPosts(Long userId, int page, int pageSize, Long currentUserId);
    PostVO getPostDetail(Long postId, Long currentUserId);
    void likePost(Long userId, Long postId);
    void unlikePost(Long userId, Long postId);
    void favoritePost(Long userId, Long postId);
    void unfavoritePost(Long userId, Long postId);
}
