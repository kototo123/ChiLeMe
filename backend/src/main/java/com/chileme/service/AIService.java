package com.chileme.service;

import java.util.List;

public interface AIService {
    String generatePostContent(List<String> imageUrls);
    String generateEncouragement(String breakfastContent, List<String> tags);
    String generateHealthAdvice(List<String> recentTags, Double weightChange);
    String auditContent(String text, List<String> imageUrls);
}
