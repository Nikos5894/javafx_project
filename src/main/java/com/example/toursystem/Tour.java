package com.example.toursystem;

public class Tour {
    private int id;
    private String name;
    private String description;
    private String date;
    private String time;
    private String accessibility;

    public Tour(int id, String name, String description, String date, String time, String accessibility) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.accessibility = accessibility;
    }

    public Tour(String name, String description, String date, String time, String accessibility) {
        this(0, name, description, date, time, accessibility);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    // Getters and Setters
    // (Автоматизуйте їх створення через IDE)
}
