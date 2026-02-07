package com.sentinel.backend.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class CreativeScorer {

    private final ChatClient chatClient;

    public CreativeScorer(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    
    public record CreativeResult(Double score) {}

    public Double scoreCreative(String content) {
        // Replace the systemPrompt in CreativeScorer.java
String systemPrompt = """
    You are a Senior Creative Director. Your task is to evaluate the 'Visual-Contextual Fit' 
    of an ad asset for a specific marketing hook.
    
    Inputs to analyze:
    - Hex Palette: [User provides these]
    - Luminance/Contrast: [User provides these]
    
    CRITICAL LOGIC:
    1. CONGRUENCE: Does the palette match the hashtag's emotion? 
       (e.g., #summer2025 needs vibrant, warm, or high-energy colors. #minimalist needs muted tones).
    2. VISUAL FRICTION: If the hashtag is 'Energetic' but colors are 'Dark/Muted', lower the score to < 0.4.
    3. STOPPING POWER: High contrast between the 'Vibrant' and 'Muted' swatches increases the score.
    
    Scoring: 
    - 0.8 to 1.0: Perfect alignment and high visual impact.
    - 0.5 to 0.7: Average; visually okay but lacks specific 'punch' for the hook.
    - 0.0 to 0.4: Strategic Mismatch; colors conflict with the intended psychological trigger.
    
    Return a JSON object with a single field 'score'.
    """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(content)
                .call()
                .entity(CreativeResult.class)
                .score();
    }
}
