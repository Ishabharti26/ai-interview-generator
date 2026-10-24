package com.isha.aiinterview.cache;

/** A group of cached questions under one category label. */
public class CachedCategory {
    private final String categoryName;
    private final java.util.List<CachedQuestion> questions;

    public CachedCategory(String categoryName, java.util.List<CachedQuestion> questions) {
        this.categoryName = categoryName;
        this.questions    = questions;
    }

    public String getCategoryName()                         { return categoryName; }
    public java.util.List<CachedQuestion> getQuestions()   { return questions; }
}
