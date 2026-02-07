package com.sentinel.backend.service;

import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.stream.IntStream;

@Service
@lombok.extern.slf4j.Slf4j
public class MonteCarloService {

    private static final int NUM_TRIALS = 10_000;
    private static final double BASE_CTR = 0.015; // 1.5% Base CTR
    private final Random random = new Random();

    public record SimulationResult(double p10, double p50, double p90) {}

    /**
     * Runs 10,000 trials to simulate ad performance.
     * 
     * @param sentimentScore (-1.0 to 1.0) Impact from audience sentiment.
     * @param creativeScore (0.0 to 1.0) Impact from visual quality.
     * @return SimulationResult with P10, P50, and P90 values.
     */
    public SimulationResult runSimulation(Double sentimentScore, Double creativeScore) {
        log.info("Running simulation -> Sentiment: {}, Creative: {}", sentimentScore, creativeScore);
        
        double[] trialResults = IntStream.range(0, NUM_TRIALS)
            .mapToDouble(i -> simulateSingleTrial(sentimentScore, creativeScore))
            .sorted()
            .toArray();

        // P10 (Pessimistic), P50 (Expected), P90 (Optimistic)
        double p10 = trialResults[(int) (NUM_TRIALS * 0.1)];
        double p50 = trialResults[(int) (NUM_TRIALS * 0.5)];
        double p90 = trialResults[(int) (NUM_TRIALS * 0.9)];

        return new SimulationResult(p10, p50, p90);
    }

    private double simulateSingleTrial(double sentiment, double creative) {
        // QS Formula Inputs with Stochastic Variance
        
        // 1. Relevance: If sentiment is negative, it's a penalty. If positive, it's a boost.
        // We use the raw sentiment for the penalty calculation later.
        // For QS (0-1), we only consider positive relevance.
        double relevance = clamp(Math.max(0, sentiment) + (random.nextGaussian() * 0.1));

        // 2. Visuals: Derived from Creative Score
        double visuals = clamp(creative + (random.nextGaussian() * 0.1));

        // 3. Copy: Derived from Creative Score
        double copy = clamp(creative + (random.nextGaussian() * 0.15));

        // Weighted Quality Score (Only Positive Contributors)
        double qs = (relevance * 0.40) + (visuals * 0.30) + (copy * 0.30);

        // Convert QS to CTR
        double volatility = random.nextGaussian() * 0.002;
        double positiveImpact = (qs * 0.05); // Up to 5% boost

        // *** NEGATIVE MODIFIER ***
        // If sentiment is negative (e.g., -0.8), it subtracts massively from performance.
        double penalty = 0.0;
        if (sentiment < 0) {
            penalty = Math.abs(sentiment) * 0.02; // Up to 2% drag
        }

        return Math.max(0.0, BASE_CTR + positiveImpact - penalty + volatility);
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
