package com.idfinance.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class PipelineExecution {

    @Id
    @GeneratedValue
    private long id;

    @Column
    private String pipeline;

    @Column
    private Status status;

    @Column
    private Date startTime;

    @Column
    private Date endTime;

    @Transient
    private List<Task> tasks;

    public PipelineExecution(String pipeline, List<Task> tasks) {
        this.pipeline = pipeline;
        this.tasks = tasks;
    }

    public PipelineExecution() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
