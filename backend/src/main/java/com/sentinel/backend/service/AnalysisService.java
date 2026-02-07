package com.sentinel.backend.service;

import com.sentinel.backend.ai.AudienceArchitect;
import com.sentinel.backend.ai.CreativeScorer;
import com.sentinel.backend.ai.SentimentAnalyst;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final InstagramServiceClient instagramService;
    private final SentimentAnalyst sentimentAnalyst;
    private final CreativeScorer creativeScorer;
    private final ImageAnalysisService imageService;
    
    // Mocking hashtag data fetch since Instagram Public Content Access is restricted
    // In a real app, this would iterate Instagram Graph API for hashtag_id/top_media
    public SentimentAnalyst.SentimentResult analyzeHashtagSentiment(String hashtag) {
        // Fetch comments via InstagramServiceClient (Autonomous Agent Architecture)
        List<String> rawComments = instagramService.searchHashtag(hashtag);
        String aggregatedComments = String.join("\n", rawComments);
        
        return sentimentAnalyst.analyzeSentiment(aggregatedComments);
    }

    public Double analyzeCreative(String imageUrl, String caption) {
        // 1. Analyze Visuals
        ImageAnalysisService.ImageProfile visualProfile = imageService.analyzeImage(imageUrl);
        
        // 2. Prepare Context for AI
        String creativeContext = String.format("The visual analysis found these colors: %s. Analysis: Luminance %.2f. Analyze how this matches the #summer2025 hashtag.", 
            visualProfile.palette().toString(), 
            visualProfile.luminance());
            
        // 3. Score
        return creativeScorer.scoreCreative(creativeContext);
    }
}
