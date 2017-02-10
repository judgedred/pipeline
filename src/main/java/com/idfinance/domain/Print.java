package com.idfinance.domain;

public class Print implements Action {
    private Task task;

    public Print(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        System.out.println(task.getName());
        task.setStatus(Status.COMPLETED);
    }

    @Override
    public Task getTask() {
        return task;
    }
}
