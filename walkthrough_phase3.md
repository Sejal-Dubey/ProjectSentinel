# Phase 3: Math Engine - Monte Carlo Simulation

## 1. The Logic (`MonteCarloService.java`)
We implemented a stochastic simulation model to predict ad performance.

### Why Monte Carlo?
In marketing, nothing is certain. "Creative A" might perform well today but fail tomorrow due to market noise. The Monte Carlo method runs **10,000 trials** with random variables to give us a probability distribution, not just a guess.

### The Algorithm:
For each trial:
1.  **Start with Base CTR**: `1.5%` (Industry standard).
2.  **Add Market Volatility**: `Random.nextGaussian()` simulates daily fluctuation.
3.  **Apply Creative Factor**:
    - High quality creatives (`creativeScore` close to 1.0) add significant boost.
4.  **Apply Sentiment Factor**:
    - **Negative Sentiment** acts as a "drag" or penalty, lowering CTR (stronger effect).
    - **Positive Sentiment** acts as a "tailwind", slightly boosting CTR.

### Output:
We return a **Result Record**:
- **P10 (Pessimistic)**: "In the worst 10% of scenarios, you will get at least X% CTR."
- **P50 (Expected)**: "Likely outcome."
- **P90 (Optimistic)**: "If you go viral, you might hit Y% CTR."

## 2. API Endpoint (`SimulationController`)
We exposed this logic via a simple REST API:
`GET /api/simulation/predict?sentiment=0.8&creative=0.9`

## Verification
You can test this endpoint locally once the server is running to see the math in action.
