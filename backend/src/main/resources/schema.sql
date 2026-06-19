CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `openid` varchar(64) NOT NULL UNIQUE COMMENT '微信openid',
  `nickname` varchar(64) DEFAULT '' COMMENT '昵称',
  `avatar` varchar(512) DEFAULT '' COMMENT '头像URL',
  `gender` tinyint DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `city` varchar(64) DEFAULT '' COMMENT '城市',
  `total_score` int DEFAULT 0 COMMENT '总积分',
  `continuous_days` int DEFAULT 0 COMMENT '当前连续打卡天数',
  `max_continuous_days` int DEFAULT 0 COMMENT '历史最高连续天数',
  `current_month_on_time` int DEFAULT 0 COMMENT '本月准时次数',
  `current_month_score` int DEFAULT 0 COMMENT '本月积分',
  `break_card_count` int DEFAULT 0 COMMENT '补签卡数量',
  `monthly_break_used` int DEFAULT 0 COMMENT '本月已补签次数',
  `status` tinyint DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_openid` (`openid`),
  INDEX `idx_score` (`total_score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `check_in` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `check_date` date NOT NULL COMMENT '打卡日期',
  `check_time` time NOT NULL COMMENT '打卡时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 1-准时 2-迟到 3-补签',
  `content` varchar(500) NOT NULL DEFAULT '' COMMENT '早餐内容（文字）',
  `image` varchar(512) DEFAULT '' COMMENT '图片URL',
  `tags` varchar(200) DEFAULT '' COMMENT '标签 逗号分隔',
  `score` int DEFAULT 0 COMMENT '获得积分',
  `ai_comment` varchar(500) DEFAULT '' COMMENT 'AI生成鼓励语',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_user_date` (`user_id`, `check_date`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_date` (`check_date`),
  INDEX `idx_user_date` (`user_id`, `check_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡记录表';

CREATE TABLE IF NOT EXISTS `post` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `content` text COMMENT '文字内容',
  `images` varchar(2048) DEFAULT '' COMMENT '图片URL列表 JSON数组',
  `tags` varchar(200) DEFAULT '' COMMENT '早餐标签 如#西式#中式',
  `ai_generated` tinyint DEFAULT 0 COMMENT '是否AI生成文案',
  `like_count` int DEFAULT 0 COMMENT '点赞数',
  `comment_count` int DEFAULT 0 COMMENT '评论数',
  `favorite_count` int DEFAULT 0 COMMENT '收藏数',
  `status` tinyint DEFAULT 1 COMMENT '状态 0-待审核 1-已发布 2-违规删除',
  `audit_result` varchar(500) DEFAULT '' COMMENT '审核结果',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status_time` (`status`, `created_at` DESC),
  INDEX `idx_hot` (`status`, `like_count` DESC, `created_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子表';

CREATE TABLE IF NOT EXISTS `comment` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `parent_id` bigint DEFAULT 0 COMMENT '父评论ID 0-顶级评论',
  `reply_to` bigint DEFAULT 0 COMMENT '回复用户ID',
  `content` varchar(500) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_post_id` (`post_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

CREATE TABLE IF NOT EXISTS `likes` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

CREATE TABLE IF NOT EXISTS `favorite` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

CREATE TABLE IF NOT EXISTS `follow` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `follower_id` bigint NOT NULL COMMENT '关注者',
  `followee_id` bigint NOT NULL COMMENT '被关注者',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_follow` (`follower_id`, `followee_id`),
  INDEX `idx_follower` (`follower_id`),
  INDEX `idx_followee` (`followee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注表';

CREATE TABLE IF NOT EXISTS `health_record` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `height` decimal(5,1) DEFAULT NULL COMMENT '身高cm',
  `weight` decimal(5,1) NOT NULL COMMENT '体重kg',
  `bmi` decimal(4,1) DEFAULT NULL COMMENT 'BMI值',
  `record_date` date NOT NULL COMMENT '记录日期',
  `ai_advice` varchar(500) DEFAULT '' COMMENT 'AI健康建议',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_user_date` (`user_id`, `record_date` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康记录表';

CREATE TABLE IF NOT EXISTS `reminder_setting` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `enabled` tinyint DEFAULT 1 COMMENT '是否开启',
  `remind_time` time NOT NULL DEFAULT '07:30:00' COMMENT '提醒时间',
  `weekdays_only` tinyint DEFAULT 1 COMMENT '仅工作日 0-每天 1-仅工作日',
  `weekend_time` time DEFAULT '08:30:00' COMMENT '周末提醒时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提醒设置表';

CREATE TABLE IF NOT EXISTS `ranking_snapshot` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `rank_type` varchar(32) NOT NULL COMMENT '排行类型 on_time/continuous/score',
  `user_id` bigint NOT NULL,
  `score_value` int NOT NULL COMMENT '排行的数值',
  `rank_num` int NOT NULL COMMENT '排名',
  `month` varchar(7) NOT NULL COMMENT '所属月份 如2026-06',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_type_month` (`rank_type`, `month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排行榜快照表';

CREATE TABLE IF NOT EXISTS `score_log` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `score` int NOT NULL COMMENT '变动积分 正负',
  `type` varchar(32) NOT NULL COMMENT '类型 check_in/check_in_late/break_card/rank_reward',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分变动记录表';

CREATE TABLE IF NOT EXISTS `notification` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '接收者',
  `type` varchar(32) NOT NULL COMMENT '类型 like/comment/follow/system',
  `from_user_id` bigint DEFAULT 0 COMMENT '触发者',
  `post_id` bigint DEFAULT 0 COMMENT '相关帖子',
  `content` varchar(500) DEFAULT '' COMMENT '通知内容',
  `is_read` tinyint DEFAULT 0 COMMENT '是否已读',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_user_id` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';
