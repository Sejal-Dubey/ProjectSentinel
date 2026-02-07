package com.sentinel.backend.repository;

import com.sentinel.backend.model.InstagramPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstagramPostRepository extends JpaRepository<InstagramPost, String> {
    
    // Custom query to find posts processed by our agents
    List<InstagramPost> findBySentimentScoreIsNotNull();

    // Find top performing posts
    List<InstagramPost> findTop10ByOrderByLikesCountDesc();
}
