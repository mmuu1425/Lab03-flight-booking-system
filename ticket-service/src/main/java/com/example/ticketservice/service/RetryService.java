package com.example.ticketservice.service;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;
import org.springframework.context.SmartLifecycle;

@Service
public class RetryService implements SmartLifecycle {
    private final BlockingQueue<RetryTask> retryQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private volatile boolean running = false;
    private static final int MAX_RETRIES = 5;
    private static final long INITIAL_DELAY = 5000; // 5秒

    // 使用@PostConstruct替代初始化方法
    @PostConstruct
    public void init() {
        start();
    }

    // 使用@PreDestroy替代销毁方法
    @PreDestroy
    public void cleanup() {
        stop();
    }

    @Override
    public void start() {
        if (!running) {
            running = true;
            executor.submit(this::processQueue);
        }
    }

    @Override
    public void stop() {
        running = false;
        executor.shutdown();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public void addTask(RetryTask task) {
        retryQueue.offer(task);
    }

    private void processQueue() {
        while (running) {
            try {
                RetryTask task = retryQueue.poll(1, TimeUnit.SECONDS);
                if (task != null) {
                    executor.submit(() -> executeWithRetry(task));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void executeWithRetry(RetryTask task) {
        int attempt = 0;
        while (attempt < MAX_RETRIES && running) {
            try {
                attempt++;
                task.execute();
                return; // 成功
            } catch (Exception e) {
                if (attempt >= MAX_RETRIES) {
                    System.err.println("Task failed after " + MAX_RETRIES + " attempts: " + e.getMessage());
                    break;
                }

                try {
                    long delay = INITIAL_DELAY * (long) Math.pow(2, attempt - 1);
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    @FunctionalInterface
    public interface RetryTask {
        void execute() throws Exception;
    }
}