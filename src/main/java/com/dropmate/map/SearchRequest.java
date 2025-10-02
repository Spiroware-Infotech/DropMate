// SearchRequest.java
package com.dropmate.map;

import jakarta.validation.constraints.NotBlank;

public class SearchRequest {
    @NotBlank(message = "Search query is required")
    private String query;
    
    private String location;
    private Integer radius;
    private String type;
    
    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Integer getRadius() { return radius; }
    public void setRadius(Integer radius) { this.radius = radius; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}