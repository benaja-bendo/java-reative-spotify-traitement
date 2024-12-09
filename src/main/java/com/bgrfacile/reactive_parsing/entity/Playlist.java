package com.bgrfacile.reactive_parsing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("playlists")
public class Playlist {
    @Id
    private Long id;
    private String name;
    private Boolean collaborative;
    private Integer pid;
    private Integer modifiedAt;
    private Integer numTracks;
    private Integer numAlbums;
    private Integer numFollowers;
    private Long infoId;
}
