// Place.java
package com.dropmate.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

public class Place {
    private String name;
    
    @JsonProperty("formatted_address")
    private String formattedAddress;
    
    private Geometry geometry;
    private double rating;
    private String[] types;
    private String businessStatus;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getFormattedAddress() { return formattedAddress; }
    public void setFormattedAddress(String formattedAddress) { this.formattedAddress = formattedAddress; }
    
    public Geometry getGeometry() { return geometry; }
    public void setGeometry(Geometry geometry) { this.geometry = geometry; }
    
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    public String[] getTypes() { return types; }
    public void setTypes(String[] types) { this.types = types; }
    
    public String getBusinessStatus() { return businessStatus; }
    public void setBusinessStatus(String businessStatus) { this.businessStatus = businessStatus; }
    
    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", formattedAddress='" + formattedAddress + '\'' +
                ", geometry=" + geometry +
                ", rating=" + rating +
                ", types=" + Arrays.toString(types) +
                '}';
    }
}