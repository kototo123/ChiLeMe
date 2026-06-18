import { $get } from './request'

export const rankApi = {
  getOnTimeRank: (topN) => $get('/rank/on-time', { topN }),
  getContinuousRank: (topN) => $get('/rank/continuous', { topN }),
  getScoreRank: (topN) => $get('/rank/score', { topN }),
  getFollowRank: (topN) => $get('/rank/follow', { topN }),
}
