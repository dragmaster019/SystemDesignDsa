import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerDemo {

    private static final int POISON_PILL = -1;

    public static void main(String[] args) throws InterruptedException {
        // BlockingQueue handles all the wait/notify + capacity bookkeeping for you — in
        // production, prefer it over hand-rolled wait()/notify() every time.
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        int itemCount = 20;

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= itemCount; i++) {
                    queue.put(i); // blocks if the queue is full — natural backpressure
                    System.out.println("Produced: " + i);
                }
                queue.put(POISON_PILL); // sentinel value: tells the consumer there's no more work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore the interrupt status, don't swallow it
            }
        }, "producer");

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    int item = queue.take(); // blocks if the queue is empty
                    if (item == POISON_PILL) break;
                    System.out.println("Consumed: " + item);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
}
