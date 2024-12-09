package com.bgrfacile.reactive_parsing.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TrackDto {
    private Integer pos;
    @JsonProperty("track_uri")
    private String trackUri;
    @JsonProperty("track_name")
    private String trackName;
    @JsonProperty("album_uri")
    private String albumUri;
    @JsonProperty("duration_ms")
    private Integer durationMs;
    @JsonProperty("album_name")
    private String albumName;
    @JsonProperty("artist_name")
    private String artistName;
    @JsonProperty("artist_uri")
    private String artistUri;
    // Getters/Setters
}
