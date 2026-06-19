package com.chileme.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private String content;
    private Long parentId;
    private Long replyTo;
}
