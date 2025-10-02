// Geometry.java
package com.dropmate.map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Geometry {
    private Location location;
    
    @JsonProperty("viewport")
    private ViewPort viewPort;
    
    // Getters and Setters
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    public ViewPort getViewPort() { return viewPort; }
    public void setViewPort(ViewPort viewPort) { this.viewPort = viewPort; }
}