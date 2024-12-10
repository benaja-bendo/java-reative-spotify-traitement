package com.bgrfacile.reactive_parsing.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("tracks")
public class Track {

    @Id
    private Integer id;

    private Integer pos;

    @Column("track_uri")
    private String trackUri;

    @Column("track_name")
    private String trackName;

    @Column("album_uri")
    private String albumUri;

    @Column("duration_ms")
    private Integer durationMs;

    @Column("album_name")
    private String albumName;

    @Column("playlist_id")
    private Long playlistId;

    @Column("artist_id")
    private Long artistId;
}
