package com.idfinance.service;

import com.idfinance.domain.PipelineExecution;

public interface PipelineExecutor {
    boolean prepare();
    boolean start();
    void stop();
    void waitToComplete();
    PipelineExecution getStatus();
}
