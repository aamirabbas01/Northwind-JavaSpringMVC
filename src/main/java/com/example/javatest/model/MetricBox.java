package com.example.javatest.model;

public class MetricBox {
    private final String title;
    private final long value;
    private final String color;
    private final String link;

    public MetricBox(String title, long value, String color, String link) {
        this.title = title;
        this.value = value;
        this.color = color;
        this.link = link;
    }

    public String getTitle() { return title; }
    public long getValue() { return value; }
    public String getColor() { return color; }
    public String getLink() { return link; }
}