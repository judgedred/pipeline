package com.idfinance.domain;

import java.util.concurrent.TimeUnit;

public class Completed implements Action {
    private Task task;

    public Completed(Task task) {
        this.task = task;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public void run() {
        System.out.println(task.getName());
        try {
            TimeUnit.SECONDS.sleep(1);
            task.setStatus(Status.COMPLETED);
        } catch(InterruptedException e) {
            task.setStatus(Status.FAILED);
        }
    }
}
