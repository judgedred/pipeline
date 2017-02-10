package com.idfinance.domain;

import java.util.concurrent.TimeUnit;

public class Delayed implements Action {
    private Task task;

    public Delayed(Task task) {
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
            TimeUnit.SECONDS.sleep(10);
            task.setStatus(Status.COMPLETED);
        } catch(InterruptedException e) {
            task.setStatus(Status.FAILED);
        }
    }
}
