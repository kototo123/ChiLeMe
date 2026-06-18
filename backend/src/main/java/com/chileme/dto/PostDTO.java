package com.chileme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class PostDTO {
    @NotBlank(message = "内容不能为空")
    private String content;

    @Size(max = 9, message = "最多上传9张图片")
    private List<String> images;

    private List<String> tags;

    private Boolean useAi;
}
