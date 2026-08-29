import java.util.concurrent.atomic.AtomicInteger;

public class RaceConditionDemo {

    // Not thread-safe: `count++` is read-modify-write — three separate steps that can interleave
    // across threads, silently losing updates.
    static int plainCount = 0;

    static synchronized void incrementSynchronized() {
        plainCount++; // the intrinsic lock on the class serializes access to this method
    }

    static final AtomicInteger atomicCount = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        int threads = 8;
        int incrementsPerThread = 10_000;
        int expected = threads * incrementsPerThread;

        plainCount = 0;
        runConcurrently(threads, incrementsPerThread, () -> plainCount++);
        System.out.println("Unsafe (raw ++):        expected " + expected + ", got " + plainCount);

        plainCount = 0;
        runConcurrently(threads, incrementsPerThread, RaceConditionDemo::incrementSynchronized);
        System.out.println("Fixed (synchronized):   expected " + expected + ", got " + plainCount);

        atomicCount.set(0);
        runConcurrently(threads, incrementsPerThread, atomicCount::incrementAndGet);
        System.out.println("Fixed (AtomicInteger):  expected " + expected + ", got " + atomicCount.get());
    }

    private static void runConcurrently(int threads, int incrementsPerThread, Runnable task) throws InterruptedException {
        Thread[] pool = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            pool[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    task.run();
                }
            });
            pool[i].start();
        }
        for (Thread t : pool) {
            t.join();
        }
    }
}
