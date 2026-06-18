package com.chileme.service;

import com.chileme.vo.RankVO;

import java.util.List;

public interface RankService {
    List<RankVO> getOnTimeRank(int topN);
    List<RankVO> getContinuousRank(int topN);
    List<RankVO> getScoreRank(int topN);
    List<RankVO> getFollowRank(Long userId, int topN);
    void refreshAllRanks();
    void persistRanks();
}
