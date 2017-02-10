package com.idfinance.domain;

import java.util.concurrent.TimeUnit;

public class Random implements Action {
    private Task task;
    private Status[] statuses = Status.values();

    public Random(Task task) {
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
            java.util.Random random = new java.util.Random();
            task.setStatus(statuses[random.nextInt(4)]);
        } catch(InterruptedException e) {
            task.setStatus(Status.FAILED);
        }
    }
}
