package com.sentinel.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "instagram_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstagramPost {

    @Id
    @Column(name = "id")
    private String id; // Instagram Media ID

    @Column(columnDefinition = "TEXT")
    private String caption;

    @Column(name = "media_type")
    private String mediaType; // IMAGE, VIDEO, CAROUSEL_ALBUM

    @Column(name = "media_url", columnDefinition = "TEXT")
    private String mediaUrl;

    @Column(name = "permalink")
    private String permalink;

    @Column(name = "likes_count")
    private Integer likesCount;

    @Column(name = "comments_count")
    private Integer commentsCount;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    // Fields for Project Sentinel Analysis
    @Column(name = "sentiment_score")
    private Double sentimentScore; // -1.0 to 1.0

    @Column(name = "audience_category")
    private String audienceCategory; // e.g., "Tech Enthusiasts"
    
    @Column(name = "creative_score")
    private Double creativeScore; // 0.0 to 1.0
}
