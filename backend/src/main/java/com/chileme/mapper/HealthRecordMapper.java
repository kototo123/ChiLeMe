package com.chileme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chileme.entity.HealthRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface HealthRecordMapper extends BaseMapper<HealthRecord> {

    @Select("SELECT * FROM health_record WHERE user_id = #{userId} ORDER BY record_date DESC")
    List<HealthRecord> selectByUserId(Long userId);
}
