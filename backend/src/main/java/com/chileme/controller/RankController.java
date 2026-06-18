package com.chileme.controller;

import com.chileme.common.result.Result;
import com.chileme.service.RankService;
import com.chileme.vo.RankVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @GetMapping("/on-time")
    public Result<List<RankVO>> getOnTimeRank(@RequestParam(defaultValue = "50") int topN) {
        return Result.success(rankService.getOnTimeRank(topN));
    }

    @GetMapping("/continuous")
    public Result<List<RankVO>> getContinuousRank(@RequestParam(defaultValue = "50") int topN) {
        return Result.success(rankService.getContinuousRank(topN));
    }

    @GetMapping("/score")
    public Result<List<RankVO>> getScoreRank(@RequestParam(defaultValue = "50") int topN) {
        return Result.success(rankService.getScoreRank(topN));
    }

    @GetMapping("/follow")
    public Result<List<RankVO>> getFollowRank(HttpServletRequest request,
                                              @RequestParam(defaultValue = "50") int topN) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(rankService.getFollowRank(userId, topN));
    }
}
