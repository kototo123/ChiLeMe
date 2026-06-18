package com.chileme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chileme.common.exception.BusinessException;
import com.chileme.entity.Comment;
import com.chileme.entity.Notification;
import com.chileme.entity.Post;
import com.chileme.mapper.CommentMapper;
import com.chileme.mapper.PostMapper;
import com.chileme.service.CommentService;
import com.chileme.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Comment addComment(Long userId, Long postId, Long parentId, Long replyTo, String content) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new BusinessException("帖子不存在");

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setParentId(parentId != null ? parentId : 0);
        comment.setReplyTo(replyTo != null ? replyTo : 0);
        comment.setContent(content);
        commentMapper.insert(comment);
        postMapper.incrementCommentCount(postId);

        if (!post.getUserId().equals(userId)) {
            Notification n = new Notification();
            n.setUserId(post.getUserId());
            n.setType("comment");
            n.setFromUserId(userId);
            n.setPostId(postId);
            n.setContent("评论了你的帖子：" + content);
            notificationService.send(n);
        }
        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) throw new BusinessException("评论不存在");
        if (!comment.getUserId().equals(userId)) throw new BusinessException("无权删除");
        commentMapper.deleteById(commentId);
    }

    @Override
    public IPage<Comment> getComments(Long postId, int page, int pageSize) {
        return commentMapper.selectPage(
                new Page<>(page, pageSize),
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .orderByAsc(Comment::getCreatedAt));
    }
}
