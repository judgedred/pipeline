package com.idfinance.dao;

import com.idfinance.domain.PipelineExecution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PipelineExecutionRepository extends CrudRepository<PipelineExecution, Long> {

}
