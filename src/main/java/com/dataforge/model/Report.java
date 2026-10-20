package com.dataforge.model;

/**
 * Model class representing a predefined report query from the problem statement.
 */
public class Report {
    private final int id;
    private final String difficulty;
    private final String title;
    private final String question;
    private final String sqlQuery;

    public Report(int id, String difficulty, String title, String question, String sqlQuery) {
        this.id = id;
        this.difficulty = difficulty;
        this.title = title;
        this.question = question;
        this.sqlQuery = sqlQuery;
    }

    public int getId() {
        return id;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getTitle() {
        return title;
    }

    public String getQuestion() {
        return question;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    @Override
    public String toString() {
        return String.format("[%2d] [%-6s] %s", id, difficulty, title);
    }
}
