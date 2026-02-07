package com.sentinel.backend.controller;

import com.sentinel.backend.service.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingestion")
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionService ingestionService;

    @PostMapping("/trigger")
    public String triggerIngestion() {
        // Run in background properly in production, but for now allow synchronous blocking for demo
        ingestionService.ingestAndAnalyze();
        return "Ingestion Pipeline Triggered Successfully";
    }
}
