package com.chileme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chileme.entity.RankSnapshot;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RankSnapshotMapper extends BaseMapper<RankSnapshot> {

    @Select("SELECT * FROM ranking_snapshot WHERE rank_type = #{rankType} AND month = #{month} ORDER BY rank_num ASC")
    List<RankSnapshot> selectByTypeAndMonth(@Param("rankType") String rankType, @Param("month") String month);
}
