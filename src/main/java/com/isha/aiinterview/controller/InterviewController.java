package com.isha.aiinterview.controller;

import com.isha.aiinterview.cache.CachedQuestion;
import com.isha.aiinterview.cache.QuestionBankService;
import com.isha.aiinterview.dto.QuestionRequest;
import com.isha.aiinterview.dto.QuestionResponse;
import com.isha.aiinterview.service.GeminiService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/questions")
@CrossOrigin(origins = "*")
public class InterviewController {

    private static final Logger log = LoggerFactory.getLogger(InterviewController.class);

    private final GeminiService geminiService;
    private final QuestionBankService questionBankService;

    public InterviewController(GeminiService geminiService, QuestionBankService questionBankService) {
        this.geminiService       = geminiService;
        this.questionBankService = questionBankService;
    }

    /**
     * POST /api/v1/questions/generate
     * Generates questions using Gemini AI, with automatic smart-fallback to the curated question bank.
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateQuestions(@Valid @RequestBody QuestionRequest request) {
        log.info("Generating questions — title: {}, level: {}, type: {}, mode: {}",
                request.getJobTitle(), request.getLevel(), request.getType(), request.getMode());
        try {
            QuestionResponse response = geminiService.generateQuestions(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating questions", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/v1/questions/bank/topics
     * Returns list of all pre-loaded topics in the revision bank (200+ questions).
     */
    @GetMapping("/bank/topics")
    public ResponseEntity<Set<String>> getTopics() {
        return ResponseEntity.ok(questionBankService.getAllTopics());
    }

    /**
     * GET /api/v1/questions/bank/topic/{topicName}
     * Returns questions for a specific topic directly without calling Gemini (0 tokens used).
     */
    @GetMapping("/bank/topic/{topicName}")
    public ResponseEntity<List<CachedQuestion>> getQuestionsByTopic(@PathVariable String topicName) {
        return ResponseEntity.ok(questionBankService.getByTopic(topicName));
    }

    /**
     * GET /api/v1/questions/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "AI Interview Generator",
                "masterBankTopics", String.valueOf(questionBankService.getAllTopics().size())
        ));
    }
}
