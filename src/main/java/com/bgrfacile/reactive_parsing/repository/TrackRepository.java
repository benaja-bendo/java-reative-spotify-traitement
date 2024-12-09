package com.bgrfacile.reactive_parsing.repository;

import com.bgrfacile.reactive_parsing.entity.Track;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TrackRepository extends ReactiveCrudRepository<Track, Long> {
}
