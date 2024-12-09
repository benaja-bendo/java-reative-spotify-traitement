package com.bgrfacile.reactive_parsing.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RootDto {
    @JsonProperty("info")
    private InfoDto info;
    @JsonProperty("playlists")
    private List<PlaylistDto> playlists;

    // Getters/Setters
}