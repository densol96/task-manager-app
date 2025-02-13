package com.accenture.backend.repository;

import com.accenture.backend.entity.Evidence;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenceRepository extends CrudRepository<Evidence, Long> {

}
