package com.sentinel.backend.controller;

import com.sentinel.backend.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.sentinel.backend.ai.SentimentAnalyst;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/hashtag")
    public SentimentAnalyst.SentimentResult analyzeHashtag(@RequestParam String tag) {
        var result = analysisService.analyzeHashtagSentiment(tag);
        
        // Broadcast to WebSocket subscribers for real-time frontend update
        messagingTemplate.convertAndSend("/topic/simulation", new RealTimeUpdate("sentiment", result.sentimentScore()));
        
        return result; // REST response for immediate check
    }

    @PostMapping("/creative")
    public AnalysisResponse analyzeCreative(@RequestBody CreativeRequest request) {
        Double score = analysisService.analyzeCreative(request.imageUrl(), request.caption());
        
        // Broadcast
        messagingTemplate.convertAndSend("/topic/simulation", new RealTimeUpdate("creative", score));
        
        return new AnalysisResponse(score);
    }
    
    public record AnalysisResponse(Double score) {}
    public record CreativeRequest(String imageUrl, String caption) {}
    public record RealTimeUpdate(String type, Double value) {}
}
