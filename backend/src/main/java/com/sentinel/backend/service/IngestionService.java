package com.sentinel.backend.service;

import com.sentinel.backend.ai.AudienceArchitect;
import com.sentinel.backend.ai.CreativeScorer;
import com.sentinel.backend.ai.SentimentAnalyst;
import com.sentinel.backend.model.InstagramPost;
import com.sentinel.backend.repository.InstagramPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngestionService {

    private final InstagramServiceClient instagramService;
    private final SentimentAnalyst sentimentAnalyst;
    private final AudienceArchitect audienceArchitect;
    private final CreativeScorer creativeScorer;
    private final ImageAnalysisService imageService;
    private final InstagramPostRepository postRepository;

    @Value("${instagram.account-id}")
    private String accountId;

    /**
     * Trigger the full ingestion and analysis pipeline.
     */
    @Transactional
    public void ingestAndAnalyze() {
        log.info("Starting Ingestion Pipeline for Account: {}", accountId);

        // 1. Fetch Raw Data
        List<Map<String, Object>> rawPosts = instagramService.getRecentPosts(accountId);
        log.info("Fetched {} posts from Instagram.", rawPosts.size());

        for (Map<String, Object> rawData : rawPosts) {
            try {
                processSinglePost(rawData);
            } catch (Exception e) {
                log.error("Failed to process post {}: {}", rawData.get("id"), e.getMessage());
            }
        }
        
        log.info("Ingestion Pipeline Completed.");
    }

    private void processSinglePost(Map<String, Object> rawData) {
        String mediaId = (String) rawData.get("id");
        String caption = (String) rawData.getOrDefault("caption", "");
        String mediaUrl = (String) rawData.getOrDefault("media_url", ""); // Ensure not null
        String mediaType = (String) rawData.get("media_type");
        String permalink = (String) rawData.get("permalink");
        // Safe casting for numbers which might be Integer or Long in JSON
        int likes = rawData.get("like_count") instanceof Number ? ((Number) rawData.get("like_count")).intValue() : 0;
        int commentsCount = rawData.get("comments_count") instanceof Number ? ((Number) rawData.get("comments_count")).intValue() : 0;

        // 2. Fetch Comments
        List<String> comments = instagramService.getComments(mediaId);
        String aggregatedComments = String.join("\n", comments);

        // 3. AI Analysis
        log.info("Analyzing Post {}: Sentiment & Creative...", mediaId);
        
        Double sentimentScore = 0.0;
        if (!comments.isEmpty()) {
            var result = sentimentAnalyst.analyzeSentiment(aggregatedComments);
            sentimentScore = result.sentimentScore();
        }

        // --- NEW: Visual Analysis using Palette API (Simulated) ---
        ImageAnalysisService.ImageProfile visualProfile = imageService.analyzeImage(mediaUrl);
        String creativeContext = String.format("Caption: %s\nVisuals: %s", caption, visualProfile.toString());
        
        Double creativeScore = creativeScorer.scoreCreative(creativeContext);
        // -----------------------------------------------------------

        String audienceProfile = audienceArchitect.generateAudienceProfile(caption + "\n---\n" + aggregatedComments);

        // 4. Persistence
        InstagramPost post = new InstagramPost();
        post.setId(mediaId);
        post.setCaption(caption);
        post.setMediaType(mediaType);
        post.setMediaUrl(mediaUrl);
        post.setPermalink(permalink);
        post.setLikesCount(likes);
        post.setCommentsCount(commentsCount);
        post.setTimestamp(LocalDateTime.now()); // Ideally parse 'timestamp' from API
        
        // AI Results
        post.setSentimentScore(sentimentScore);
        post.setCreativeScore(creativeScore);
        post.setAudienceCategory(audienceProfile.substring(0, Math.min(audienceProfile.length(), 255))); // Truncate for DB

        postRepository.save(post);
        log.info("Saved analyzed post: {}", mediaId);
    }
}
