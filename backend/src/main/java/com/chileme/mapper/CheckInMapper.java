package com.chileme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chileme.entity.CheckIn;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface CheckInMapper extends BaseMapper<CheckIn> {

    @Select("SELECT * FROM check_in WHERE user_id = #{userId} AND check_date = #{date}")
    CheckIn selectByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Select("SELECT * FROM check_in WHERE user_id = #{userId} " +
            "AND check_date >= #{startDate} AND check_date <= #{endDate} " +
            "ORDER BY check_date DESC")
    List<CheckIn> selectByUserAndDateRange(@Param("userId") Long userId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    @Select("SELECT COUNT(*) FROM check_in WHERE user_id = #{userId} " +
            "AND status = 1 AND check_date >= #{startDate} AND check_date <= #{endDate}")
    Integer countOnTimeByMonth(@Param("userId") Long userId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    @Select("SELECT COUNT(DISTINCT c.tags) FROM check_in c WHERE c.user_id = #{userId} " +
            "AND c.check_date >= #{startDate} AND c.check_date <= #{endDate} AND c.tags != ''")
    Integer countDistinctTagsByMonth(@Param("userId") Long userId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    @Select("SELECT * FROM check_in WHERE user_id = #{userId} " +
            "ORDER BY check_date DESC LIMIT 1")
    CheckIn selectLastCheckIn(Long userId);

    @Select("SELECT * FROM check_in WHERE user_id = #{userId} AND status = 1 " +
            "ORDER BY check_date DESC LIMIT 1")
    CheckIn selectLastOnTimeCheckIn(Long userId);
}
