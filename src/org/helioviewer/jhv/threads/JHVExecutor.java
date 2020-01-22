package org.helioviewer.jhv.threads;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JHVExecutor {

    public static final ExecutorService cachedPool = createCachedPool();

    private static ExecutorService createCachedPool() {
        ExecutorService service = Executors.newCachedThreadPool(new JHVThread.NamedThreadFactory("Worker"));
        shutdownOnDisposal(service);
        return service;
    }

    private static void shutdownOnDisposal(ExecutorService es) {
        Runnable shutdownHook =
                new Runnable() {
                    final WeakReference<ExecutorService> executorServiceRef = new WeakReference<>(es);

                    @Override
                    public void run() {
                        ExecutorService executorService = executorServiceRef.get();
                        if (executorService != null) {
                            AccessController.doPrivileged(
                                    (PrivilegedAction<Void>) () -> {
                                        executorService.shutdown();
                                        return null;
                                    });
                        }
                    }
                };

        AccessController.doPrivileged(
                (PrivilegedAction<Void>) () -> {
                    Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
                    return null;
                });
    }

}
