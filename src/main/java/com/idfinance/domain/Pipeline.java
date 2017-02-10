package com.idfinance.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Pipeline {

    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
    private String name;

    @Column
    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Task> tasks;

    public Pipeline(String name, String description, List<Task> tasks) {
        this.name = name;
        this.description = description;
        this.tasks = tasks;
    }

    public Pipeline() {
    }

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
