package com.idfinance.service;

import com.idfinance.domain.Pipeline;
import com.idfinance.domain.PipelineContext;
import org.springframework.stereotype.Service;

@Service
public class PipelineServiceImpl implements PipelineService {

    @Override
    public Pipeline createPipeline(PipelineContext pipelineContext) {
        Pipeline pipeline = new Pipeline(pipelineContext.getName(), pipelineContext.getDescription(), pipelineContext.getTasks());
        pipelineContext.getTransitions().forEach((k, v) -> {
            pipeline.getTasks().forEach(task -> {
                if(v.getName().equals(task.getName())) {
                    task.getDependencies().add(k.getName());
                }
            });
        });
        return pipeline;
    }
}
