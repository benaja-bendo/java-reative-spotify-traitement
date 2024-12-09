package com.bgrfacile.reactive_parsing.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("artists")
public class Artist {
    @Id
    private Long id;
    private String artistName;
    private String artistUri;
}
