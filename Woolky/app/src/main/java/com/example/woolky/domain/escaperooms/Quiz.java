package com.example.woolky.domain.escaperooms;

import java.util.List;

public class Quiz {

    private String question;
    private List<String> answers;
    private int indexOfCorrectAnswer;

    public Quiz() {}

    public Quiz(String question, List<String> answers, int indexOfCorrectAnswer) {
        this.question = question;
        this.answers = answers;
        this.indexOfCorrectAnswer = indexOfCorrectAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public int getIndexOfCorrectAnswer() {
        return indexOfCorrectAnswer;
    }
}
