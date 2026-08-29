import java.util.concurrent.CompletableFuture;

public class CompletableFutureDemo {

    static CompletableFuture<String> fetchUser(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(100); // simulate a network/DB call
            if (userId.equals("bad-user")) {
                throw new RuntimeException("user not found: " + userId);
            }
            return "User(" + userId + ")";
        });
    }

    static CompletableFuture<String> fetchOrders(String user) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(100);
            return "Orders for " + user;
        });
    }

    public static void main(String[] args) throws Exception {
        // Chained, non-blocking composition: fetch the user, THEN fetch their orders — no thread
        // ever blocks waiting on the first call. thenCompose flattens the nested future instead
        // of returning a CompletableFuture<CompletableFuture<String>>.
        CompletableFuture<String> pipeline = fetchUser("42")
                .thenCompose(CompletableFutureDemo::fetchOrders)
                .thenApply(String::toUpperCase);

        System.out.println(pipeline.get()); // .get() blocks only here, at the very end, to print it

        // Combine two independent async calls that don't depend on each other's result.
        CompletableFuture<String> userFuture = fetchUser("7");
        CompletableFuture<String> ordersFuture = fetchOrders("7");
        CompletableFuture<String> combined = userFuture.thenCombine(ordersFuture,
                (user, orders) -> user + " | " + orders);
        System.out.println(combined.get());

        // Exception handling in a chain: exceptionally() recovers with a fallback value.
        CompletableFuture<String> recovered = fetchUser("bad-user")
                .exceptionally(ex -> "fallback-user (" + ex.getCause().getMessage() + ")");
        System.out.println(recovered.get());

        // handle() sees BOTH outcomes (result may be null on failure, ex may be null on success) —
        // useful when the recovery logic needs to branch on which case happened.
        CompletableFuture<String> handled = fetchUser("bad-user")
                .handle((result, ex) -> ex != null ? "handled failure: " + ex.getCause().getMessage() : result);
        System.out.println(handled.get());
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
