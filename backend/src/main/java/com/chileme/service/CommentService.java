package com.chileme.service;

import com.chileme.entity.Comment;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface CommentService {
    Comment addComment(Long userId, Long postId, Long parentId, Long replyTo, String content);
    void deleteComment(Long userId, Long commentId);
    IPage<Comment> getComments(Long postId, int page, int pageSize);
}
