package com.chileme.service.impl;

import com.chileme.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class AIServiceImpl implements AIService {

    private static final Random RANDOM = new Random();
    private static final String[] ENCOURAGEMENTS = {
        "早安，今天也很棒哦！🥪",
        "营养满满的早餐，给你一天好心情！🌟",
        "又是元气满满的一天！💪",
        "好好吃早餐的人运气都不会差～🎯",
        "今日早餐能量已充值成功！⚡"
    };

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;

    public AIServiceImpl(RestTemplateBuilder builder,
                         @Value("${spring.ai.openai.api-key:}") String apiKey,
                         @Value("${spring.ai.openai.base-url:https://api.openai.com}") String baseUrl) {
        this.restTemplate = builder.build();
        this.apiKey = apiKey;
        this.apiUrl = baseUrl + "/v1/chat/completions";
    }

    @Override
    public String generatePostContent(List<String> imageUrls) {
        return callAI("我上传了几张早餐图片，请帮我生成一段有趣的早餐文案用于社交分享，" +
                "字数控制在50字以内，风格轻松活泼，可以包含一些emoji。");
    }

    @Override
    public String generateEncouragement(String breakfastContent, List<String> tags) {
        String tagStr = tags != null && !tags.isEmpty() ? String.join("、", tags) : "";
        return callAI(String.format(
                "用户刚刚打卡了早餐，内容：%s，标签：%s。请生成一句简短（20字以内）的鼓励语或趣味点评，语气温暖可爱。",
                breakfastContent, tagStr));
    }

    @Override
    public String generateHealthAdvice(List<String> recentTags, Double weightChange) {
        String tagStr = recentTags != null && !recentTags.isEmpty() ?
                String.join("、", recentTags) : "暂无记录";
        String weightStr = weightChange != null ?
                (weightChange > 0 ? "体重上升了" + String.format("%.1f", weightChange) + "kg" :
                 weightChange < 0 ? "体重下降了" + String.format("%.1f", Math.abs(weightChange)) + "kg" :
                 "体重保持稳定") : "暂无变化数据";
        return callAI(String.format(
                "用户近期的早餐标签有：%s，%s。请给出简短（30字以内）的营养建议。", tagStr, weightStr));
    }

    @Override
    public String auditContent(String text, List<String> imageUrls) {
        return callAI(String.format(
                "请审核以下内容是否包含违规信息（色情、暴力、广告、政治敏感等），" +
                "如果安全请回复'通过'，否则回复'不通过'并说明原因。\n内容：%s", text));
    }

    private String callAI(String prompt) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("sk-placeholder")) {
            return mockResponse(prompt);
        }
        try {
            Map<String, Object> request = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.7
            );
            Map resp = restTemplate.postForObject(apiUrl, request, Map.class);
            if (resp != null) {
                List choices = (List) resp.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map first = (Map) choices.get(0);
                    Map message = (Map) first.get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            log.warn("AI call failed, using mock: {}", e.getMessage());
        }
        return mockResponse(prompt);
    }

    private String mockResponse(String prompt) {
        if (prompt.contains("文案")) return "今天的早餐看起来不错！";
        if (prompt.contains("鼓励") || prompt.contains("点评"))
            return ENCOURAGEMENTS[RANDOM.nextInt(ENCOURAGEMENTS.length)];
        if (prompt.contains("营养")) return "均衡饮食，保持健康！";
        if (prompt.contains("审核")) return "通过（自动审核）";
        return "早安！";
    }
}
