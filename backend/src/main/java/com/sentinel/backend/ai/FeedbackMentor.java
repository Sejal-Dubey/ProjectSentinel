package com.sentinel.backend.ai;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FeedbackMentor {

    private final ChatClient chatClient;

    public FeedbackMentor(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String provideFeedback(String simulationContext) {
        // Replace the systemPrompt in FeedbackMentor.java
String systemPrompt = """
    You are a Senior Marketing Strategist. Use "Pyramid Prompting" to interpret 
    Monte Carlo simulation results for a non-technical user.
    
    CONTEXT: You are analyzing the gap between P10 (Worst Case) and P90 (Best Case).
    
    STRUCTURE RULES:
    1. VERDICT: "Safe", "Volatile", or "Doomed".
       - If Creative AND Sentiment are > 0.6: MUST be "Safe".
       - If one is high and one is low: MUST be "Volatile".
       - If both are < 0.4: MUST be "Doomed".
       
    2. WHY: Explain the 'Psychographic Drag'. 
       - If Sentiment is low: "The audience feels disconnected from this specific topic."
       - If Creative is low: "The visual palette fails to trigger the 'stop-scroll' instinct."
       
    3. PIVOT: Give a professional 'Creative Pivot' (e.g., 'Increase saturation' or 'Change hashtag').

    Rule: If Creative Score > 0.7, the Verdict is "Safe" because high-impact visuals 
    can often carry a neutral sentiment. 

    Combine into one concise, punchy paragraph. Use professional but simple language.
    """;

        try {
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(simulationContext)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("AI Mentor Offline/Error: {}", e.getMessage());
            return """
                1. Verdict: Offline (System Error)
                2. Why: AI engine unreachable.
                3. Pivot: Check backend logs & API Keys.
                """;
        }
    }
}
