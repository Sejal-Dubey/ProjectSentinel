package com.sentinel.backend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class InstagramServiceClient {

    private final RestClient restClient;
    private final String accessToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InstagramServiceClient(RestClient.Builder builder, 
                                  @Value("${instagram.api.base-url}") String baseUrl,
                                  @Value("${instagram.access-token}") String accessToken) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.accessToken = accessToken;
    }

    /**
     * Fetches recent posts for an account.
     */
    public List<Map<String, Object>> getRecentPosts(String accountId) {
        if (accessToken.equals("mock_token")) {
            return getMockPosts();
        }

        try {
            // Fields to fetch
            String fields = "id,caption,media_type,media_url,permalink,like_count,comments_count,timestamp";
            
            JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/{accountId}/media")
                    .queryParam("fields", fields)
                    .queryParam("access_token", accessToken)
                    .build(accountId))
                .retrieve()
                .body(JsonNode.class);

            if (response != null && response.has("data")) {
                return StreamSupport.stream(response.get("data").spliterator(), false)
                    .map(node -> objectMapper.convertValue(node, new TypeReference<Map<String, Object>>() {}))
                    .collect(Collectors.toList());
            }
            return Collections.emptyList();
            
        } catch (Exception e) {
            System.err.println("Error fetching posts: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Fetches comments for a specific media object.
     */
    public List<String> getComments(String mediaId) {
        if (accessToken.equals("mock_token")) {
            return Collections.singletonList("This is a mock comment because no token was provided.");
        }

        try {
            JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/{mediaId}/comments")
                    .queryParam("fields", "text")
                    .queryParam("access_token", accessToken)
                    .build(mediaId))
                .retrieve()
                .body(JsonNode.class);

            if (response != null && response.has("data")) {
                return StreamSupport.stream(response.get("data").spliterator(), false)
                    .map(node -> node.get("text").asText())
                    .collect(Collectors.toList());
            }
            return Collections.emptyList();

        } catch (Exception e) {
            System.err.println("Error fetching comments: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> getMockPosts() {
        return List.of(
            Map.of("id", "12345", "caption", "Love this product! #awesome", "comments_count", 10),
            Map.of("id", "67890", "caption", "Not what I expected...", "comments_count", 5)
        );
    }

    /**
     * Search for top media by hashtag.
     */
    public List<String> searchHashtag(String hashtag) {
        if (accessToken.equals("mock_token")) {
            return getMockHashtagComments(hashtag);
        }
        // In a real scenario, we would call the Graph API here.
        // For now, even with a token, we fall back to mock for this specific specialized endpoint 
        // to avoid permission complexity in this phase.
        return getMockHashtagComments(hashtag);
    }

    private List<String> getMockHashtagComments(String hashtag) {
        return switch (hashtag.toLowerCase()) {
            case "love" -> List.of(
                "This is absolutely amazing! #love",
                "Best thing I've seen all day.",
                "Incredible work, really inspiring.",
                "So much love for this!",
                "Perfect! 10/10"
            );
            case "fail" -> List.of(
                "This is a disaster. #fail",
                "Worst experience ever.",
                "Do not buy this.",
                "Complete waste of money.",
                "Disappointed."
            );
            case "sale" -> List.of(
                "Great prices!",
                "Just bought one.",
                "Shipping is a bit high though.",
                "Good deal.",
                "Is this still available?"
            );
            default -> List.of(
                "Interesting post about " + hashtag,
                "Looks okay.",
                "Not sure what to think.",
                "Cool.",
                "What is this?"
            );
        };
    }
}
