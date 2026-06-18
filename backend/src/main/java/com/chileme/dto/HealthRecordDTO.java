package com.chileme.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HealthRecordDTO {
    @DecimalMin(value = "50.0", message = "身高范围50-250cm")
    @DecimalMax(value = "250.0", message = "身高范围50-250cm")
    private BigDecimal height;

    @NotNull(message = "体重不能为空")
    @DecimalMin(value = "10.0", message = "体重范围10-300kg")
    @DecimalMax(value = "300.0", message = "体重范围10-300kg")
    private BigDecimal weight;

    private LocalDate recordDate;
}
