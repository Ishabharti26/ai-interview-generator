package com.isha.aiinterview.dto;

import java.util.List;

public class QuestionResponse {

    private String jobTitle;
    private String level;
    private String type;
    private String language;
    private String source;
    private boolean fallback;
    private String notice;
    private List<QuestionCategory> categories;

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public String getJobTitle()                          { return jobTitle; }
    public void   setJobTitle(String v)                  { this.jobTitle = v; }
    public String getLevel()                             { return level; }
    public void   setLevel(String v)                     { this.level = v; }
    public String getType()                              { return type; }
    public void   setType(String v)                      { this.type = v; }
    public String getLanguage()                          { return language; }
    public void   setLanguage(String v)                  { this.language = v; }
    public String getSource()                            { return source; }
    public void   setSource(String v)                    { this.source = v; }
    public boolean isFallback()                          { return fallback; }
    public void   setFallback(boolean v)                 { this.fallback = v; }
    public String getNotice()                            { return notice; }
    public void   setNotice(String v)                    { this.notice = v; }
    public List<QuestionCategory> getCategories()        { return categories; }
    public void   setCategories(List<QuestionCategory> v){ this.categories = v; }

    // ── Builder ───────────────────────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final QuestionResponse obj = new QuestionResponse();
        public Builder jobTitle(String v)                    { obj.jobTitle   = v; return this; }
        public Builder level(String v)                       { obj.level      = v; return this; }
        public Builder type(String v)                        { obj.type       = v; return this; }
        public Builder language(String v)                    { obj.language   = v; return this; }
        public Builder source(String v)                      { obj.source     = v; return this; }
        public Builder fallback(boolean v)                   { obj.fallback   = v; return this; }
        public Builder notice(String v)                      { obj.notice     = v; return this; }
        public Builder categories(List<QuestionCategory> v)  { obj.categories = v; return this; }
        public QuestionResponse build()                      { return obj; }
    }

    // ── Nested: QuestionCategory ──────────────────────────────────────────────
    public static class QuestionCategory {
        private String  categoryName;
        private List<QA> questions;

        public String    getCategoryName()           { return categoryName; }
        public void      setCategoryName(String v)   { this.categoryName = v; }
        public List<QA>  getQuestions()              { return questions; }
        public void      setQuestions(List<QA> v)    { this.questions = v; }

        public static CategoryBuilder builder() { return new CategoryBuilder(); }
        public static class CategoryBuilder {
            private final QuestionCategory obj = new QuestionCategory();
            public CategoryBuilder categoryName(String v)  { obj.categoryName = v; return this; }
            public CategoryBuilder questions(List<QA> v)   { obj.questions    = v; return this; }
            public QuestionCategory build()                { return obj; }
        }
    }

    // ── Nested: QA ───────────────────────────────────────────────────────────
    public static class QA {
        private int    number;
        private String question;
        private String answer;

        public int    getNumber()             { return number; }
        public void   setNumber(int v)        { this.number = v; }
        public String getQuestion()           { return question; }
        public void   setQuestion(String v)   { this.question = v; }
        public String getAnswer()             { return answer; }
        public void   setAnswer(String v)     { this.answer = v; }

        public static QABuilder builder() { return new QABuilder(); }
        public static class QABuilder {
            private final QA obj = new QA();
            public QABuilder number(int v)       { obj.number   = v; return this; }
            public QABuilder question(String v)  { obj.question = v; return this; }
            public QABuilder answer(String v)    { obj.answer   = v; return this; }
            public QA build()                    { return obj; }
        }
    }
}
