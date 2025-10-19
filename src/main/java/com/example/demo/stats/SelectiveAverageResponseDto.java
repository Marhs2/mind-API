package com.example.demo.stats;

public class SelectiveAverageResponseDto {
    private double averageBefore;
    private double averageAfter;
    private String improvement;
    // getter, setter
    public double getAverageBefore() { return averageBefore; }
    public void setAverageBefore(double averageBefore) { this.averageBefore = averageBefore; }
    public double getAverageAfter() { return averageAfter; }
    public void setAverageAfter(double averageAfter) { this.averageAfter = averageAfter; }
    public String getImprovement() { return improvement; }
    public void setImprovement(String improvement) { this.improvement = improvement; }
}
