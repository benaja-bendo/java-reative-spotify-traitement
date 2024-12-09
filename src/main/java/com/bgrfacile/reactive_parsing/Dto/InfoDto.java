package com.bgrfacile.reactive_parsing.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InfoDto {
    @JsonProperty("generated_on")
    private String generatedOn;
    @JsonProperty("slice")
    private String slice;
    @JsonProperty("version")
    private String version;
    // Getters/Setters
}

