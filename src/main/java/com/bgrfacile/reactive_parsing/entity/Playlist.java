package com.bgrfacile.reactive_parsing.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@NoArgsConstructor
@Table("playlists")
public class Playlist {

    @Id
    private Long id;

    private String name;

    private Boolean collaborative;

    private Integer pid;

    @Column("modified_at")
    private Integer modifiedAt;

    @Column("num_tracks")
    private Integer numTracks;

    @Column("num_albums")
    private Integer numAlbums;

    @Column("num_followers")
    private Integer numFollowers;

    @Column("info_id")
    private Long infoId;
}
