package com.example.datajdbcpractice.domain.id_generation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinionRepository extends CrudRepository<Minion, Long> {
}
