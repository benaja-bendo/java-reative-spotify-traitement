package com.bgrfacile.reactive_parsing.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PlaylistDto {
    private String name;
    private Boolean collaborative;
    private Integer pid;
    @JsonProperty("modified_at")
    private Integer modifiedAt;
    @JsonProperty("num_tracks")
    private Integer numTracks;
    @JsonProperty("num_albums")
    private Integer numAlbums;
    @JsonProperty("num_followers")
    private Integer numFollowers;
    private List<TrackDto> tracks;
    // Getters/Setters
}
