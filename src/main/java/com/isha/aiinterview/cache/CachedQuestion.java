package com.isha.aiinterview.cache;

/** A single cached Q&A entry. */
public class CachedQuestion {
    private final int    number;
    private final String question;
    private final String answer;

    public CachedQuestion(int number, String question, String answer) {
        this.number   = number;
        this.question = question;
        this.answer   = answer;
    }

    public int    getNumber()   { return number; }
    public String getQuestion() { return question; }
    public String getAnswer()   { return answer; }
}
