// ViewPort.java
package com.dropmate.map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ViewPort {
    @JsonProperty("northeast")
    private Location northeast;
    
    @JsonProperty("southwest")
    private Location southwest;
    
    // Getters and Setters
    public Location getNortheast() { return northeast; }
    public void setNortheast(Location northeast) { this.northeast = northeast; }
    
    public Location getSouthwest() { return southwest; }
    public void setSouthwest(Location southwest) { this.southwest = southwest; }
}