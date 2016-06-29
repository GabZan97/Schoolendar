package com.gabrielezanelli.schoolendar;

public class Task {
    private String text;
    private boolean completed;

    public Task() {

    }

    public Task(String text, boolean completed) {
        this.text = text;
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
