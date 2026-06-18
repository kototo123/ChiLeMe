package com.chileme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chileme.common.exception.BusinessException;
import com.chileme.dto.PostDTO;
import com.chileme.entity.*;
import com.chileme.enums.PostStatusEnum;
import com.chileme.mapper.*;
import com.chileme.service.AIService;
import com.chileme.service.NotificationService;
import com.chileme.service.PostService;
import com.chileme.vo.PostVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final LikesMapper likesMapper;
    private final FavoriteMapper favoriteMapper;
    private final NotificationService notificationService;
    private final AIService aiService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PostVO createPost(Long userId, PostDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        String content = dto.getContent();
        if (Boolean.TRUE.equals(dto.getUseAi())) {
            content = aiService.generatePostContent(dto.getImages());
        }

        String auditResult = aiService.auditContent(content, dto.getImages());
        boolean pass = auditResult.contains("通过") || auditResult.contains("pass");

        String imagesJson = "";
        try {
            imagesJson = dto.getImages() != null ? objectMapper.writeValueAsString(dto.getImages()) : "";
        } catch (JsonProcessingException e) {
            log.error("序列化图片列表失败", e);
        }

        String tagsStr = dto.getTags() != null ? String.join(",", dto.getTags()) : "";

        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setImages(imagesJson);
        post.setTags(tagsStr);
        post.setAiGenerated(Boolean.TRUE.equals(dto.getUseAi()) ? 1 : 0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setFavoriteCount(0);
        post.setStatus(pass ? PostStatusEnum.PUBLISHED.getCode() : PostStatusEnum.PENDING.getCode());
        post.setAuditResult(auditResult);
        postMapper.insert(post);

        return toPostVO(post, user, false, false);
    }

    @Override
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new BusinessException("帖子不存在");
        if (!post.getUserId().equals(userId)) throw new BusinessException("无权删除");
        postMapper.deleteById(postId);
    }

    @Override
    public IPage<PostVO> getFeed(int page, int pageSize, Long currentUserId) {
        Page<Post> p = postMapper.selectPage(
                new Page<>(page, pageSize),
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getStatus, PostStatusEnum.PUBLISHED.getCode())
                        .orderByDesc(Post::getCreatedAt));
        return convertToVOPage(p, currentUserId);
    }

    @Override
    public IPage<PostVO> getUserPosts(Long userId, int page, int pageSize, Long currentUserId) {
        Page<Post> p = postMapper.selectPage(
                new Page<>(page, pageSize),
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getUserId, userId)
                        .eq(Post::getStatus, PostStatusEnum.PUBLISHED.getCode())
                        .orderByDesc(Post::getCreatedAt));
        return convertToVOPage(p, currentUserId);
    }

    @Override
    public PostVO getPostDetail(Long postId, Long currentUserId) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new BusinessException("帖子不存在");
        User user = userMapper.selectById(post.getUserId());
        boolean liked = currentUserId != null && likesMapper.exists(postId, currentUserId);
        boolean favorited = currentUserId != null && favoriteMapper.exists(postId, currentUserId);
        return toPostVO(post, user, liked, favorited);
    }

    @Override
    @Transactional
    public void likePost(Long userId, Long postId) {
        if (likesMapper.exists(postId, userId)) throw new BusinessException("已点赞");
        Likes like = new Likes();
        like.setPostId(postId);
        like.setUserId(userId);
        likesMapper.insert(like);
        postMapper.incrementLikeCount(postId);

        Post post = postMapper.selectById(postId);
        if (post != null && !post.getUserId().equals(userId)) {
            Notification n = new Notification();
            n.setUserId(post.getUserId());
            n.setType("like");
            n.setFromUserId(userId);
            n.setPostId(postId);
            n.setContent("赞了你的帖子");
            notificationService.send(n);
        }
    }

    @Override
    @Transactional
    public void unlikePost(Long userId, Long postId) {
        likesMapper.delete(
                new LambdaQueryWrapper<Likes>()
                        .eq(Likes::getPostId, postId)
                        .eq(Likes::getUserId, userId));
        postMapper.decrementLikeCount(postId);
    }

    @Override
    @Transactional
    public void favoritePost(Long userId, Long postId) {
        if (favoriteMapper.exists(postId, userId)) throw new BusinessException("已收藏");
        Favorite fav = new Favorite();
        fav.setPostId(postId);
        fav.setUserId(userId);
        favoriteMapper.insert(fav);
        postMapper.incrementFavoriteCount(postId);
    }

    @Override
    @Transactional
    public void unfavoritePost(Long userId, Long postId) {
        favoriteMapper.delete(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getPostId, postId)
                        .eq(Favorite::getUserId, userId));
        postMapper.decrementFavoriteCount(postId);
    }

    private IPage<PostVO> convertToVOPage(Page<Post> page, Long currentUserId) {
        List<PostVO> vos = page.getRecords().stream().map(post -> {
            User user = userMapper.selectById(post.getUserId());
            boolean liked = currentUserId != null && likesMapper.exists(post.getId(), currentUserId);
            boolean favorited = currentUserId != null && favoriteMapper.exists(post.getId(), currentUserId);
            return toPostVO(post, user, liked, favorited);
        }).collect(Collectors.toList());

        Page<PostVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(vos);
        return result;
    }

    @SuppressWarnings("unchecked")
    private PostVO toPostVO(Post post, User user, boolean liked, boolean favorited) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setUserNickname(user != null ? user.getNickname() : "");
        vo.setUserAvatar(user != null ? user.getAvatar() : "");
        vo.setContent(post.getContent());
        try {
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                vo.setImages(objectMapper.readValue(post.getImages(), List.class));
            }
        } catch (JsonProcessingException e) {
            vo.setImages(new ArrayList<>());
        }
        if (post.getTags() != null && !post.getTags().isEmpty()) {
            vo.setTags(Arrays.asList(post.getTags().split(",")));
        }
        vo.setAiGenerated(post.getAiGenerated() == 1);
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setFavoriteCount(post.getFavoriteCount());
        vo.setLiked(liked);
        vo.setFavorited(favorited);
        vo.setCreatedAt(post.getCreatedAt());
        return vo;
    }
}
