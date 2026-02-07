package com.sentinel.backend.tool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

@Service
public class SentimentTool implements Function<SentimentTool.Request, String> {

    private static final Logger log = LoggerFactory.getLogger(SentimentTool.class);
    private final RestClient restClient;
    private final String apiKey;

    public SentimentTool(@Value("${serpapi.api-key:mock_key}") String apiKey) {
        this.restClient = RestClient.create();
        this.apiKey = apiKey;
    }

    @Override
    public String apply(Request request) {
        return searchInstagramContext(request.hashtag());
    }

    public String searchInstagramContext(String hashtag) {
        log.info("Searching Instagram context for hashtag: {}", hashtag);

        if ("mock_key".equals(apiKey)) {
            return getMockData(hashtag);
        }

        try {
            // Search query: site:instagram.com "{hashtag}" comments
            String query = String.format("site:instagram.com \"%s\" comments", hashtag);
            
            JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("serpapi.com")
                    .path("/search.json")
                    .queryParam("engine", "google")
                    .queryParam("q", query)
                    .queryParam("api_key", apiKey)
                    .build())
                .retrieve()
                .body(JsonNode.class);

            if (response != null && response.has("organic_results")) {
                StringBuilder sb = new StringBuilder();
                response.get("organic_results").forEach(node -> {
                    if (node.has("snippet")) {
                        sb.append(node.get("snippet").asText()).append("\n");
                    }
                });
                return sb.toString();
            }
            return "No recent results found.";

        } catch (Exception e) {
            log.error("SerpApi search failed", e);
            return "Failed to fetch search results. Using fallback data.";
        }
    }

    private String getMockData(String hashtag) {
        return switch (hashtag.toLowerCase()) {
            case "love", "awesome" -> "Everyone is loving this product! So amazing. Best purchase ever. Highly recommend! #love";
            case "fail", "disaster" -> "This is the worst. Don't buy it. A complete waste of money. I'm so angry. #fail";
            default -> "Some people are talking about " + hashtag + ". It's interesting. Mixed reviews seen online.";
        };
    }

    public record Request(String hashtag) {}
}
