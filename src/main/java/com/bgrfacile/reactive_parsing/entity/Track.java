package com.bgrfacile.reactive_parsing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("tracks")
public class Track {
    @Id
    private Long id;
    private Integer pos;
    private String trackUri;
    private String trackName;
    private String albumUri;
    private Integer durationMs;
    private String albumName;
    private Long playlistId;
    private Long artistId;
}
