package com.bgrfacile.reactive_parsing.repository;

import com.bgrfacile.reactive_parsing.entity.Info;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface InfoRepository extends ReactiveCrudRepository<Info, Long> {
}
