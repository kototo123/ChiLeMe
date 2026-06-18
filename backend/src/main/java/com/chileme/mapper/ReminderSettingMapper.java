package com.chileme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chileme.entity.ReminderSetting;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ReminderSettingMapper extends BaseMapper<ReminderSetting> {

    @Select("SELECT * FROM reminder_setting WHERE enabled = 1")
    List<ReminderSetting> selectAllEnabled();
}
