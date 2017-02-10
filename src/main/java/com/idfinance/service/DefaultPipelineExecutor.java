package com.idfinance.service;

import com.idfinance.domain.*;
import org.apache.log4j.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultPipelineExecutor implements PipelineExecutor {
    private static final Logger logger = Logger.getLogger(DefaultPipelineExecutor.class);
    private final PipelineExecution pipelineExecution;
    private final DirectedGraph graph = new SimpleDirectedGraph(DefaultEdge.class);
    private final Set<Task> executing = new HashSet<>();
    private ThreadExecutor executor;
    private final ActionFactory actionFactory;

    public DefaultPipelineExecutor(ActionFactory actionFactory, PipelineExecution pipelineExecution) {
        this.actionFactory = actionFactory;
        this.pipelineExecution = pipelineExecution;
    }

    @Override
    public boolean prepare() {
        Map<String, Task> tasks = new HashMap<>();
        for(Task task : pipelineExecution.getTasks()) {
            if(tasks.put(task.getName(), task) != null) {
                throw new RuntimeException("Task with same name already exists: " + task.getName());
            }
        }
        buildGraph(tasks);
        return true;
    }

    @Override
    public boolean start() {
        int cpus = Runtime.getRuntime().availableProcessors();
        executor = new ThreadExecutor(cpus, 60, new LinkedBlockingQueue());
        pipelineExecution.setStatus(Status.PENDING);
        scheduleTasks();
        return true;
    }

    @Override
    public void waitToComplete() {
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PipelineExecution getStatus() {
        return pipelineExecution;
    }

    @Override
    public void stop() {
        pipelineExecution.setStatus(Status.SKIPPED);
        logger.info("Execution status: " + pipelineExecution.getStatus());
        executor.shutdown();
    }

    private boolean buildGraph(Map<String, Task> tasks) {
        for(Task task : tasks.values()) {
            graph.addVertex(task);
        }
        for (Task task: tasks.values()) {
            if (task.getDependencies() != null) {
                for (String depend: task.getDependencies()) {
                    Task dependOnTask = tasks.get(depend);
                    graph.addEdge(dependOnTask, task);
                }
            }
        }

        return true;
    }

    private void scheduleTasks() {
        if (graph.vertexSet().size() == 0) {
            pipelineExecution.setEndTime(new Date());
            pipelineExecution.setStatus(Status.COMPLETED);
            logger.info("Execution status: " + pipelineExecution.getStatus());
            executor.shutdown();
        }

        synchronized (graph) {
            Iterator<Task> iter = new TopologicalOrderIterator(graph);

            while(iter.hasNext()) {
                Task task = iter.next();
                if(graph.incomingEdgesOf(task).size() == 0 && !executing.contains(task) && !pipelineExecution.getStatus().equals(Status.SKIPPED)) {
                    task.setStatus(Status.IN_PROGRESS);
                    task.setStartTime(new Date());
                    pipelineExecution.setStatus(Status.IN_PROGRESS);
                    executing.add(task);
                    executor.execute(actionFactory.getAction(ActionType.valueOf(task.getAction().get("type")), task));
                    if(pipelineExecution.getStartTime() == null) {
                        pipelineExecution.setStartTime(new Date());
                    }
                } else {
                    task.setStatus(Status.PENDING);
                }
            }
        }
    }

    private void completed(Task t) {
        logger.info("Completed Task: " + t.getName() + " with status " + t.getStatus());
        t.setEndTime(new Date());
        logger.info("Execution status: " + pipelineExecution.getStatus());

        synchronized (graph) {
            graph.removeVertex(t);
            executing.remove(t);
        }

        if(!t.getStatus().equals(Status.FAILED)) {
            scheduleTasks();
        } else {
            pipelineExecution.setStatus(Status.FAILED);
            logger.info("Execution status: " + pipelineExecution.getStatus());
            logger.info("Shutdown executor...");
            executor.shutdown();
        }
    }

    private void failed(Task t, Throwable e) {
        t.setStatus(Status.FAILED);
        pipelineExecution.setStatus(Status.FAILED);
        logger.fatal("Failed Task: " + t.getName(), e);
        logger.info("Execution status: " + pipelineExecution.getStatus());
        logger.info("Shutdown executor...");
        executor.shutdown();
    }

    private class ThreadExecutor extends ThreadPoolExecutor {

        public ThreadExecutor(int corePoolSize, long keepAliveSeconds, BlockingQueue workQueue) {
            super(corePoolSize, corePoolSize, keepAliveSeconds, TimeUnit.SECONDS, workQueue);
        }

        @Override
        protected void beforeExecute(Thread thread, Runnable runTask) {
            super.beforeExecute(thread, runTask);

            Action action = (Action)runTask;
            Task task = action.getTask();

            logger.info("Starting task: " + task.getName());
        }

        @Override
        protected void afterExecute(Runnable runTask, Throwable e) {
            super.afterExecute(runTask, e);

            Action action = (Action)runTask;
            Task task = action.getTask();

            if (e == null) {
                completed(task);
            } else {
                failed(task, e);
            }
        }

    }


}
