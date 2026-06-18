package com.chileme.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostVO {
    private Long id;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private String content;
    private List<String> images;
    private List<String> tags;
    private Boolean aiGenerated;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private Boolean liked;
    private Boolean favorited;
    private LocalDateTime createdAt;
}
