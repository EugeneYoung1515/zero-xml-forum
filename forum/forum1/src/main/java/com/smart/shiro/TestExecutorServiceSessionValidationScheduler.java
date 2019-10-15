package com.smart.shiro;

import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler;
import org.apache.shiro.session.mgt.SessionValidationScheduler;

public class TestExecutorServiceSessionValidationScheduler extends ExecutorServiceSessionValidationScheduler implements SessionValidationScheduler, Runnable {
    @Override
    public void run() {
        System.out.println("run");
        long startTime = System.currentTimeMillis();
        System.out.println("before");
        System.out.println(getSessionManager());
        getSessionManager().validateSessions();
        long stopTime = System.currentTimeMillis();
        System.out.println("all");
    }
}
