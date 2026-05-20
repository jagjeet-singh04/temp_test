package com.bnpp.assessment.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

/**
 * * Global scheduler that fires a *single* tick at a fixed rate (default = 50s)
 * * and invokes every registered Runnable on the **EDT**.
 * *
 * * All UI components that need periodic refresh should register a Runnable
 * * with {@code RefreshScheduler.getInstance().register(...)}.
 * *
 * * The scheduler runs on a dedicated background thread, therefore the
 * * registration / deregistration operations are thread-safe.
 */
public final class RefreshScheduler {

    private static final RefreshScheduler INSTANCE = new RefreshScheduler();

    /** How often the whole UI should refresh (in seconds). */
    private static final int REFRESH_PERIOD_SECONDS = 5;

    private final ScheduledExecutorService executor = 
        Executors.newSingleThreadScheduledExecutor();

    /** Runnables that will be executed on every tick. */
    private final List<Runnable> tasks = new CopyOnWriteArrayList<>();

    private RefreshScheduler() {
        // The very first tick happens after REFRESH_PERIOD_SECONDS,
        // then every REFRESH_PERIOD_SECONDS thereafter.
        executor.scheduleAtFixedRate(this::runAll, 
            REFRESH_PERIOD_SECONDS, 
            REFRESH_PERIOD_SECONDS, 
            TimeUnit.SECONDS);
    }

    public static RefreshScheduler getInstance() {
        return INSTANCE;
    }

    /**
     * Register a refresh task - it will be called on **every** tick.
     */
    public void register(Runnable task) {
        tasks.add(task);
    }

    /**
     * Remove a previously registered task (e.g. when a dialog is closed).
     */
    public void deregister(Runnable task) {
        tasks.remove(task);
    }

    /**
     * Called by the executor - executes every task on the Swing EDT.
     */
    private void runAll() {
        for (Runnable r : tasks) {
            SwingUtilities.invokeLater(r);
        }
    }

    /**
     * Graceful shutdown - call on application exit (optional).
     */
    public void shutdown() {
        executor.shutdownNow();
    }
}