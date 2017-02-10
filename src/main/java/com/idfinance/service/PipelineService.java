package com.idfinance.service;

import com.idfinance.domain.Pipeline;
import com.idfinance.domain.PipelineContext;

public interface PipelineService {
    Pipeline createPipeline(PipelineContext pipelineContext);
}
