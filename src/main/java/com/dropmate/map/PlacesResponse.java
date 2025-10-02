// PlacesResponse.java
package com.dropmate.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PlacesResponse {
    private List<Place> results;
    private String status;
    
    @JsonProperty("error_message")
    private String errorMessage;
    
    @JsonProperty("next_page_token")
    private String nextPageToken;
    
    // Getters and Setters
    public List<Place> getResults() { return results; }
    public void setResults(List<Place> results) { this.results = results; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getNextPageToken() { return nextPageToken; }
    public void setNextPageToken(String nextPageToken) { this.nextPageToken = nextPageToken; }
}