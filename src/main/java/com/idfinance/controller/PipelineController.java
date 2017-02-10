package com.idfinance.controller;

import com.idfinance.dao.PipelineExecutionRepository;
import com.idfinance.dao.PipelineRepository;
import com.idfinance.domain.Pipeline;
import com.idfinance.domain.PipelineContext;
import com.idfinance.domain.PipelineExecution;
import com.idfinance.service.ActionFactory;
import com.idfinance.service.DefaultPipelineExecutor;
import com.idfinance.service.PipelineExecutor;
import com.idfinance.service.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/pipeline")
public class PipelineController {

    private final ActionFactory actionFactory;
    private final PipelineRepository pipelineRepository;
    private final PipelineExecutionRepository pipelineExecutionRepository;
    private final PipelineService pipelineService;
    private final ConcurrentMap<Long, PipelineExecutor> executors = new ConcurrentHashMap<>();

    @Autowired
    public PipelineController(ActionFactory actionFactory, PipelineRepository pipelineRepository, PipelineExecutionRepository pipelineExecutionRepository, PipelineService pipelineService) {
        this.actionFactory = actionFactory;
        this.pipelineRepository = pipelineRepository;
        this.pipelineExecutionRepository = pipelineExecutionRepository;
        this.pipelineService = pipelineService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Pipeline create(@RequestBody PipelineContext pipelineContext) {
        Pipeline pipeline = pipelineService.createPipeline(pipelineContext);
        return pipelineRepository.save(pipeline);
    }

    @RequestMapping(value = "/update" , method = RequestMethod.PUT)
    public Pipeline update(@RequestBody PipelineContext pipelineContext) {
        Pipeline pipeline = pipelineService.createPipeline(pipelineContext);
        Pipeline pipelinePersistant = pipelineRepository.findByName(pipelineContext.getName());
        pipeline.setId(pipelinePersistant.getId());
        return pipelineRepository.save(pipeline);
    }

    @RequestMapping(value = "/read/{pipelineName}" , method = RequestMethod.GET)
    public Pipeline read(@PathVariable String pipelineName) {
        return pipelineRepository.findByName(pipelineName);
    }

    @RequestMapping(value = "/delete/{pipelineName}" , method = RequestMethod.DELETE)
    public String delete(@PathVariable String pipelineName) {
        Pipeline pipeline = pipelineRepository.findByName(pipelineName);
        pipelineRepository.delete(pipeline);
        return pipeline.getName() + " deleted.";
    }

    @RequestMapping(value = "/execute/{pipelineName}", method = RequestMethod.POST)
    public PipelineExecution execute(@PathVariable String pipelineName) {
        Pipeline pipeline = pipelineRepository.findByName(pipelineName);
        PipelineExecution pipelineExecution = new PipelineExecution(pipeline.getName(), pipeline.getTasks());
        PipelineExecutor pipelineExecutor = new DefaultPipelineExecutor(actionFactory, pipelineExecution);
        pipelineExecutor.prepare();
        pipelineExecutor.start();
        PipelineExecution pipelineExecutionSaved = pipelineExecutionRepository.save(pipelineExecution);
        executors.putIfAbsent(pipelineExecutionSaved.getId(), pipelineExecutor);
        return pipelineExecutionSaved;
    }

    @RequestMapping(value = "/show/{executionId}", method = RequestMethod.GET)
    public PipelineExecution show(@PathVariable Long executionId) {
            return executors.get(executionId).getStatus();
    }

    @RequestMapping(value = "/stop/{executionId}", method = RequestMethod.GET)
    public PipelineExecution stop(@PathVariable Long executionId) {
        PipelineExecutor pipelineExecutor = executors.get(executionId);
        pipelineExecutor.stop();
        pipelineExecutor.waitToComplete();
        PipelineExecution pipelineExecution = executors.get(executionId).getStatus();
        pipelineExecutionRepository.save(pipelineExecution);
        executors.remove(executionId);
        return pipelineExecution;
    }
}
