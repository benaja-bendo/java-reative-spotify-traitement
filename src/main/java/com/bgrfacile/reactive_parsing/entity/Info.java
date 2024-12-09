package com.bgrfacile.reactive_parsing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("info")
public class Info {
    @Id
    private Long id;
    private String generatedOn;
    private String slice;
    private String version;
}
