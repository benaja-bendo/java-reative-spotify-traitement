package com.bgrfacile.reactive_parsing.repository;

import com.bgrfacile.reactive_parsing.entity.Artist;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ArtistRepository extends ReactiveCrudRepository<Artist, Long> {
    Mono<Artist> findByArtistUri(String artistUri);
}
