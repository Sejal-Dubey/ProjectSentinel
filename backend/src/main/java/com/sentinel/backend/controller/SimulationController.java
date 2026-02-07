package com.sentinel.backend.controller;

import com.sentinel.backend.service.MonteCarloService;
import com.sentinel.backend.service.MonteCarloService.SimulationResult;
import com.sentinel.backend.ai.FeedbackMentor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SimulationController {

    private final MonteCarloService monteCarloService;
    private final FeedbackMentor feedbackMentor;

    @GetMapping("/predict")
    public SimulationResponse predict(
            @RequestParam Double sentiment,
            @RequestParam Double creative) {
        
        var result = monteCarloService.runSimulation(sentiment, creative);
        
        // Context for the AI
        String context = String.format(
            "Sentiment Score: %.2f, Creative Score: %.2f. Result P10: %.2f%%, P90: %.2f%%.", 
            sentiment, creative, result.p10() * 100, result.p90() * 100
        );
        
        String aiFeedback = feedbackMentor.provideFeedback(context);
        
        return new SimulationResponse(result, aiFeedback);
    }
    
    public record SimulationResponse(MonteCarloService.SimulationResult metrics, String mentorFeedback) {}
}
