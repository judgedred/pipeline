package com.idfinance.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineContext {
    private String name;
    private String description;
    private List<Task> tasks;
    private Map<Node, Node> transitions = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Map<Node, Node> getTransitions() {
        return transitions;
    }

    public void setTransitions(Map<Node, Node> transitions) {
        this.transitions = transitions;
    }
}
