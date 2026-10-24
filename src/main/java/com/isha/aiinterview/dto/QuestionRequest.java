package com.isha.aiinterview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class QuestionRequest {

    @NotBlank(message = "Job title is required")
    @Size(max = 100, message = "Job title must be at most 100 characters")
    private String jobTitle;

    private String level    = "junior";
    private String type     = "mixed";
    private String language = "English";

    /**
     * Mode:
     * - "auto" (default): Tries Gemini AI; if quota/key fails, seamlessly falls back to cache.
     * - "cache": Always uses curated question bank (0 AI tokens consumed).
     * - "ai": Strict AI mode.
     */
    private String mode     = "auto";

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public String getJobTitle()              { return jobTitle; }
    public void   setJobTitle(String v)      { this.jobTitle = v; }

    public String getLevel()                 { return level; }
    public void   setLevel(String v)         { this.level = v; }

    public String getType()                  { return type; }
    public void   setType(String v)          { this.type = v; }

    public String getLanguage()              { return language; }
    public void   setLanguage(String v)      { this.language = v; }

    public String getMode()                  { return mode; }
    public void   setMode(String v)          { this.mode = v; }
}
