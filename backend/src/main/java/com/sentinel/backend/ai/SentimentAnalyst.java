package com.sentinel.backend.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SentimentAnalyst {

    private final ChatClient chatClient;

    public SentimentAnalyst(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public record SentimentResult(Double sentimentScore, String audienceCategory) {}

    public SentimentResult analyzeSentiment(String comments) {
        // Replace the systemPrompt in SentimentAnalyst.java
         String systemPrompt = """
    You are a Social Media Psychographer. Analyze these Instagram comments to determine 
    the 'Emotional Temperature' of the audience.
    
    Metrics:
    - -1.0 to -0.1: Hostile/Skeptical (Haters/Cynics)
    - 0.0 to 0.3: Passive/Bored (Neutral Observers)
    - 0.4 to 1.0: Enthusiastic/Intentional (Fanatics/Buyers)
    
    Determine the 'Audience Category' and the 'Sentiment Score'. 
    If you detect high 'Purchase Intent' (e.g., "Where can I buy?"), the score MUST be > 0.7.
    """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(comments)
                .call()
                .entity(SentimentResult.class);
    }
}
