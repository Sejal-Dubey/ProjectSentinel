package com.sentinel.backend.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AudienceArchitect {

    private final ChatClient chatClient;

    public AudienceArchitect(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generateAudienceProfile(String postContent) {
        // Replace the systemPrompt in AudienceArchitect.java
          String systemPrompt = """
    You are an elite Demographer. Construct a 'Digital Twin' persona of the 
    most likely person to click on this post.
    
    Format:
    - Archetype: [Name]
    - Core Desire: [What they want]
    - Age/Vibe: [Demographic context]
    
    Keep it under 40 words.
    """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(postContent)
                .call()
                .content();
    }
}
