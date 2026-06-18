package com.chileme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chileme.entity.User;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<User> {

    @Update("UPDATE user SET total_score = total_score + #{score}, " +
            "current_month_score = current_month_score + #{score} " +
            "WHERE id = #{userId}")
    void addScore(Long userId, Integer score);

    @Update("UPDATE user SET continuous_days = continuous_days + 1, " +
            "max_continuous_days = CASE WHEN continuous_days + 1 > max_continuous_days THEN continuous_days + 1 ELSE max_continuous_days END, " +
            "current_month_on_time = current_month_on_time + 1 " +
            "WHERE id = #{userId}")
    void incrementContinuousDays(Long userId);

    @Update("UPDATE user SET continuous_days = 0 WHERE id = #{userId}")
    void resetContinuousDays(Long userId);

    @Update("UPDATE user SET break_card_count = break_card_count + #{count} WHERE id = #{userId}")
    void addBreakCards(Long userId, Integer count);

    @Update("UPDATE user SET monthly_break_used = monthly_break_used + 1, break_card_count = break_card_count - 1 WHERE id = #{userId}")
    void useBreakCard(Long userId);

    @Update("UPDATE user SET current_month_on_time = 0, current_month_score = 0 WHERE id = #{userId}")
    void resetMonthlyStats(Long userId);
}
