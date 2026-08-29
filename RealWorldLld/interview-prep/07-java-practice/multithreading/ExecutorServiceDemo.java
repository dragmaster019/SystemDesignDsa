import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceDemo {

    public static void main(String[] args) throws InterruptedException {
        // Fixed pool: bounded concurrency, threads reused across tasks. The production default
        // when you want a hard cap on parallelism instead of a thread-per-task explosion.
        ExecutorService pool = Executors.newFixedThreadPool(4);

        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            final int taskId = i;
            tasks.add(() -> {
                System.out.println("Task " + taskId + " running on " + Thread.currentThread().getName());
                Thread.sleep(100); // simulate work
                if (taskId == 6) {
                    throw new IllegalStateException("task 6 failed on purpose");
                }
                return taskId * taskId;
            });
        }

        try {
            List<Future<Integer>> futures = pool.invokeAll(tasks);
            int total = 0;
            for (Future<Integer> f : futures) {
                try {
                    total += f.get(); // blocks until that task's result is ready
                } catch (ExecutionException e) {
                    // A task threw — the real exception is wrapped, unwrap it via getCause()
                    System.out.println("Task failed: " + e.getCause());
                }
            }
            System.out.println("Sum of squares (excluding the failed task): " + total);
        } finally {
            // Never skip this in a long-lived app: an un-shutdown pool's threads are non-daemon
            // by default and will keep the JVM alive / leak resources across repeated use.
            pool.shutdown();
            if (!pool.awaitTermination(2, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        }
    }
}
