# Phase 1, Step 3: LangChain4j Integration Walkthrough

## 1. Declarative AI Services (`@AiService`)
Instead of writing complex code to call the OpenAI API, parse the JSON, and handle errors, we used **LangChain4j's High-Level API**.

### The `SentimentAnalyst` Interface
We defined a Java interface that looks like magic:
```java
@AiService
public interface SentimentAnalyst {
    @SystemMessage("...")
    Double analyzeSentiment(@UserMessage String comments);
}
```
**How it works:**
1.  **`@AiService`**: LangChain4j creates a dynamic proxy (an implementation on the fly) for this interface.
2.  **`@SystemMessage`**: This sets the "System Prompt" sent to the LLM (GPT-4). We instructed it to act as a "Social Media Psychographer".
3.  **Return Type `Double`**: We asked the LLM to return a number. LangChain4j automatically parses the string response from the LLM into a `Double`.

## 2. Configuration (`LangChainConfig`)
We set up the `ChatLanguageModel` bean.
- **Model**: `gpt-4o-mini` (Fast and cheap).
- **API Key**: Pulled from `application.properties` or environment variables with `${OPENAI_API_KEY:demo}`.

## 3. Why this is better than "Hardcoded"
- **Flexibility**: Changing the logic is as simple as editing the English prompt in the `@SystemMessage`.
- **Clean Code**: No HTTP clients, no JSON parsing libraries cluttering our business logic.
- **Type Safety**: The rest of our Java app deals with a `Double`, not a raw String.

## Verification
- We added the `langchain4j-spring-boot-starter` in Step 1.
- The code compiles (we will verify this next).
