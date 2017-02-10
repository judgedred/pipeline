package com.idfinance.dao;

import com.idfinance.domain.Pipeline;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PipelineRepository extends CrudRepository<Pipeline, Long> {
    Pipeline findByName(String name);
}
