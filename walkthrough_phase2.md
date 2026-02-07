# Phase 2: Multi-Agent System (MAS) Walkthrough

## 1. The Expert Team
We have assembled a team of three specialized AI Agents using LangChain4j. Each "Agent" is a declarative Java interface powered by a specific System Prompt.

### A. The Sentimentalist (`SentimentAnalyst`)
*   **Role**: Psychographer.
*   **Input**: Comments.
*   **Output**: `Double` (-1.0 to 1.0).
*   **Goal**: Determine if the audience actually likes the brand or is just making noise.

### B. The Audience Architect (`AudienceArchitect`)
*   **Role**: Profiler.
*   **Input**: Post Content (Caption + Comments).
*   **Output**: `String` (Persona Description).
*   **Goal**: Figure out *who* we are talking to.
    *   *Self-Correction*: Initially, we might just return a String. In the future, we could return a strictly typed Java `Record` (e.g., `AudienceProfile`) for easier database storage.

### C. The Creative Director (`CreativeScorer`)
*   **Role**: Quality Control.
*   **Input**: Caption.
*   **Output**: `Double` (0.0 to 1.0).
*   **Goal**: Predict "Virality". Only high-quality creatives passed this gate get high budgets in the simulation.

## 2. Integration Strategy
In the next phase (Math Engine), we will combine these three signals:
$$
\text{Predicted CTR} = (\text{Sentiment Score} \times 0.4) + (\text{Creative Score} \times 0.6)
$$
*(This is a simplified preliminary formula for the Monte Carlo method)*.
