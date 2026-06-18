package com.chileme.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class CheckInDTO {
    @NotBlank(message = "早餐内容不能为空")
    private String content;

    private String image;
    private List<String> tags;
}
