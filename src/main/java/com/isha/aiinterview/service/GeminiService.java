package com.isha.aiinterview.service;

import com.isha.aiinterview.cache.CachedCategory;
import com.isha.aiinterview.cache.CachedQuestion;
import com.isha.aiinterview.cache.QuestionBankService;
import com.isha.aiinterview.dto.QuestionRequest;
import com.isha.aiinterview.dto.QuestionResponse;
import com.isha.aiinterview.dto.QuestionResponse.QA;
import com.isha.aiinterview.dto.QuestionResponse.QuestionCategory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final WebClient geminiWebClient;
    private final ObjectMapper objectMapper;
    private final QuestionBankService questionBankService;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-1.5-flash}")
    private String model;

    public GeminiService(WebClient geminiWebClient, ObjectMapper objectMapper, QuestionBankService questionBankService) {
        this.geminiWebClient     = geminiWebClient;
        this.objectMapper        = objectMapper;
        this.questionBankService = questionBankService;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Generates interview questions.
     * If mode is "cache", serves instantly from the 200+ curated question bank (0 AI tokens used).
     * If mode is "auto", tries Google Gemini API first; if Gemini hits token limit, invalid key,
     * or network error, it seamlessly falls back to the curated master cache so the user is never blocked.
     */
    public QuestionResponse generateQuestions(QuestionRequest req) {
        String mode = req.getMode() != null ? req.getMode().toLowerCase() : "auto";

        if ("cache".equals(mode)) {
            log.info("Mode 'cache' requested: serving directly from curated bank (saving AI tokens)");
            return generateFromCache(req, "Curated Master Bank (Zero Tokens Used)", false,
                    "Loaded from pre-configured master revision checklist.");
        }

        try {
            log.info("Attempting Gemini API generation for role '{}'...", req.getJobTitle());
            String prompt  = buildPrompt(req);
            String rawJson = callGemini(prompt);
            QuestionResponse response = parseGeminiResponse(rawJson, req);

            if (response.getCategories() != null && !response.getCategories().isEmpty()) {
                response.setSource("Google Gemini 1.5 Flash (AI Generated)");
                response.setFallback(false);
                response.setNotice("Freshly generated tailored questions via Gemini AI.");
                return response;
            } else {
                throw new RuntimeException("Gemini returned empty categories");
            }

        } catch (Exception e) {
            log.warn("Gemini API call failed or exhausted ({}). Seamlessly activating curated question cache.", e.getMessage());

            if ("ai".equals(mode)) {
                // If the user explicitly demanded AI only, bubble error
                throw new RuntimeException("Gemini AI failed: " + e.getMessage() + ". Please check API key or switch to Master Cache mode.");
            }

            // Fallback: return high-yield topic-matched questions from master bank
            return generateFromCache(req, "Curated Master Bank (Smart Cache Fallback)", true,
                    "Gemini AI free-tier quota protected. Served high-yield verified questions from the master revision bank.");
        }
    }

    /**
     * Build response from the pre-loaded 200+ question bank.
     */
    public QuestionResponse generateFromCache(QuestionRequest req, String source, boolean isFallback, String notice) {
        List<CachedCategory> cachedCats = questionBankService.selectCategories(req.getJobTitle(), req.getType(), 4);
        List<QuestionCategory> categories = new ArrayList<>();

        for (CachedCategory cc : cachedCats) {
            List<QA> qas = new ArrayList<>();
            for (CachedQuestion cq : cc.getQuestions()) {
                qas.add(QA.builder()
                        .number(cq.getNumber())
                        .question(cq.getQuestion())
                        .answer(cq.getAnswer())
                        .build());
            }
            categories.add(QuestionCategory.builder()
                    .categoryName(cc.getCategoryName())
                    .questions(qas)
                    .build());
        }

        return QuestionResponse.builder()
                .jobTitle(req.getJobTitle())
                .level(req.getLevel())
                .type(req.getType())
                .language(req.getLanguage())
                .source(source)
                .fallback(isFallback)
                .notice(notice)
                .categories(categories)
                .build();
    }

    // ── Prompt Building ───────────────────────────────────────────────────────

    private String buildPrompt(QuestionRequest req) {
        String categoryInstructions = buildCategoryInstructions(req.getType());
        return """
                You are a senior technical interviewer simulating a real-world 3-4 LPA fresher / campus interview for the following role:

                Target Job Title: %s
                Candidate Level: %s
                Interview Style: %s
                Language: %s

                %s

                STRICT STRUCTURE RULES:
                1. Respond ONLY with valid JSON — no markdown fences, no backticks, no commentary.
                2. Use this exact schema:
                {
                  "categories": [
                    {
                      "categoryName": "Round 1: Introduction & HR Fundamentals",
                      "questions": [
                        { "number": 1, "question": "...", "answer": "..." },
                        { "number": 2, "question": "...", "answer": "..." },
                        { "number": 3, "question": "...", "answer": "..." },
                        { "number": 4, "question": "...", "answer": "..." },
                        { "number": 5, "question": "...", "answer": "..." }
                      ]
                    }
                  ]
                }
                3. CRITICAL: Every single category MUST have EXACTLY 5 questions (numbered 1 to 5).
                4. For each question, provide an actionable, interview-ready model answer (2-3 sentences explaining the core concept clearly).
                5. Keep the difficulty perfectly aligned with a 3-4 LPA freshers interview (%s level).
                6. All text must be in %s language.
                """.formatted(
                req.getJobTitle(), req.getLevel(), req.getType(), req.getLanguage(),
                categoryInstructions,
                req.getLevel(), req.getLanguage()
        );
    }

    private String buildCategoryInstructions(String type) {
        return """
                Generate questions in these EXACT 5 sequential interview rounds (5 questions each, 25 total):
                1. Round 1: Introduction & HR Fundamentals (Tell me about yourself, resume walkthrough, why this role/tech stack, strengths/weaknesses, goals)
                2. Round 2: Core Subject - Java & OOP Principles (Four OOP pillars, == vs .equals, String immutability/pool, interface vs abstract class, exception handling)
                3. Round 3: DBMS & SQL Fundamentals (ACID properties, primary/foreign/unique keys, normalization 1NF-3NF, 2nd highest salary SQL query, DELETE vs TRUNCATE vs DROP)
                4. Round 4: Data Structures & Problem Solving (Basic DSA) (Array vs LinkedList, 2nd largest in array, palindrome 2-pointer, HashMap collisions, Kadane's max subarray sum)
                5. Round 5: Resume Project Discussion & Spring Boot Architecture (Project explanation, Spring Boot vs Spring, annotations @RestController/@Service/@Repository, HTTP methods, handling challenges/bugs)
                """;
    }

    // ── Gemini HTTP Call ──────────────────────────────────────────────────────

    private String callGemini(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 4096,
                        "responseMimeType", "application/json"
                )
        );

        String url = "/v1beta/models/%s:generateContent?key=%s".formatted(model, apiKey);

        return geminiWebClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.isError(), response ->
                        response.bodyToMono(String.class).flatMap(err -> {
                            log.error("Gemini API HTTP {}: {}", response.statusCode(), err);
                            return Mono.error(new RuntimeException("Gemini HTTP " + response.statusCode() + ": " + err));
                        })
                )
                .bodyToMono(JsonNode.class)
                .map(node -> node.at("/candidates/0/content/parts/0/text").asText())
                .block();
    }

    // ── Response Parsing ──────────────────────────────────────────────────────

    private QuestionResponse parseGeminiResponse(String rawJson, QuestionRequest req) {
        try {
            String json = rawJson.trim()
                    .replaceAll("(?s)^```json\\s*", "")
                    .replaceAll("(?s)```$", "")
                    .trim();

            JsonNode root          = objectMapper.readTree(json);
            JsonNode categoriesNode = root.get("categories");
            List<QuestionCategory> categories = new ArrayList<>();

            if (categoriesNode != null && categoriesNode.isArray()) {
                for (JsonNode catNode : categoriesNode) {
                    String catName = catNode.path("categoryName").asText("Questions");
                    List<QA> questions = new ArrayList<>();
                    JsonNode qsNode = catNode.path("questions");
                    if (qsNode.isArray()) {
                        for (JsonNode qNode : qsNode) {
                            questions.add(QA.builder()
                                    .number(qNode.path("number").asInt())
                                    .question(qNode.path("question").asText())
                                    .answer(qNode.path("answer").asText())
                                    .build());
                        }
                    }
                    categories.add(QuestionCategory.builder()
                            .categoryName(catName)
                            .questions(questions)
                            .build());
                }
            }

            return QuestionResponse.builder()
                    .jobTitle(req.getJobTitle())
                    .level(req.getLevel())
                    .type(req.getType())
                    .language(req.getLanguage())
                    .categories(categories)
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse Gemini JSON: {}", rawJson, e);
            throw new RuntimeException("Failed to parse AI response. Please try again.", e);
        }
    }
}
