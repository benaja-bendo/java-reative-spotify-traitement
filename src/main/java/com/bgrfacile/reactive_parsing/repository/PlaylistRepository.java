package com.bgrfacile.reactive_parsing.repository;

import com.bgrfacile.reactive_parsing.entity.Playlist;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PlaylistRepository extends ReactiveCrudRepository<Playlist, Long> {
}
