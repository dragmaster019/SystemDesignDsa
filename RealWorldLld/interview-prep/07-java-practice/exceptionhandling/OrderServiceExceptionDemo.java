import java.io.IOException;

// Base exception: unchecked (extends RuntimeException) is the typical production choice for
// domain/business errors, so the service layer isn't forced to declare `throws` at every level.
// It carries a stable error code — that's what callers/API contracts should key off of, not the
// exception's class name or message text.
class ServiceException extends RuntimeException {
    private final String errorCode;

    public ServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause); // preserve the original cause — never lose it
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

class OrderNotFoundException extends ServiceException {
    public OrderNotFoundException(String orderId) {
        super("ORDER_NOT_FOUND", "Order not found: " + orderId);
    }
}

class InsufficientInventoryException extends ServiceException {
    public InsufficientInventoryException(String sku, int requested, int available) {
        super("INSUFFICIENT_INVENTORY",
                "Requested " + requested + " of " + sku + " but only " + available + " available");
    }
}

// Checked exception example: models a recoverable, external I/O-style failure.
class InventoryLookupException extends Exception {
    public InventoryLookupException(String message, Throwable cause) {
        super(message, cause);
    }
}

// AutoCloseable resource so we can demonstrate try-with-resources doing real cleanup.
class InventoryConnection implements AutoCloseable {
    private final String label;

    public InventoryConnection(String label) {
        this.label = label;
        System.out.println("  [" + label + "] connection opened");
    }

    public int checkStock(String sku) throws InventoryLookupException {
        if (sku.equals("SKU-BAD")) {
            // Simulate a low-level failure (e.g. a network timeout). Wrap it, never swallow it.
            throw new InventoryLookupException("stock lookup failed for " + sku,
                    new IOException("connection reset by peer"));
        }
        return sku.equals("SKU-LOW") ? 1 : 50;
    }

    @Override
    public void close() {
        System.out.println("  [" + label + "] connection closed");
    }
}

class OrderService {

    private static final int MAX_RETRIES = 3;

    public void placeOrder(String orderId, String sku, int quantity) {
        if (orderId == null || orderId.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }

        int available = checkStockWithRetry(sku);

        if (quantity > available) {
            throw new InsufficientInventoryException(sku, quantity, available);
        }

        System.out.println("Order placed: " + orderId + " (" + quantity + "x " + sku + ")");
    }

    // Retry pattern for a transient, checked failure — common in production for flaky network calls.
    private int checkStockWithRetry(String sku) {
        InventoryLookupException lastFailure = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            // try-with-resources: the connection is guaranteed to close, even when checkStock() throws.
            try (InventoryConnection conn = new InventoryConnection("attempt-" + attempt)) {
                return conn.checkStock(sku);
            } catch (InventoryLookupException e) {
                lastFailure = e;
                System.out.println("  attempt " + attempt + " failed: " + e.getMessage()
                        + " (cause: " + e.getCause() + ")");
            }
        }

        // Exhausted retries: wrap into a domain exception, preserving the *original* low-level
        // cause so it's still visible in logs/stack traces three layers up.
        throw new ServiceException("INVENTORY_UNAVAILABLE",
                "Inventory service unavailable after " + MAX_RETRIES + " attempts", lastFailure);
    }

    // Multi-catch: two unrelated exception types handled identically without duplicating the
    // block. (NumberFormatException is intentionally not listed alongside it here — it's a
    // subclass of IllegalArgumentException, and Java forbids listing a subclass with its own
    // superclass in one multi-catch, since the superclass branch would already cover it.)
    static void parsePort(String raw) {
        try {
            int port = Integer.parseInt(raw);
            if (port < 0 || port > 65535) {
                throw new IllegalArgumentException("port out of range: " + port);
            }
            System.out.println("Parsed port: " + port);
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Invalid config: " + e.getMessage());
        }
    }
}

public class OrderServiceExceptionDemo {
    public static void main(String[] args) {
        OrderService service = new OrderService();

        System.out.println("-- 1. happy path --");
        service.placeOrder("ORD-1", "SKU-OK", 5);

        System.out.println("\n-- 2. business rule violation (unchecked, no throws needed) --");
        try {
            service.placeOrder("ORD-2", "SKU-LOW", 10);
        } catch (InsufficientInventoryException e) {
            System.out.println("Handled: [" + e.getErrorCode() + "] " + e.getMessage());
        }

        System.out.println("\n-- 3. missing input --");
        try {
            service.placeOrder("", "SKU-OK", 1);
        } catch (OrderNotFoundException e) {
            System.out.println("Handled: [" + e.getErrorCode() + "] " + e.getMessage());
        }

        System.out.println("\n-- 4. transient failure exhausts retries, cause preserved --");
        try {
            service.placeOrder("ORD-4", "SKU-BAD", 1);
        } catch (ServiceException e) {
            System.out.println("Handled: [" + e.getErrorCode() + "] " + e.getMessage());
            System.out.println("Root cause: " + e.getCause());
        }

        System.out.println("\n-- 5. multi-catch --");
        OrderService.parsePort("8080");
        OrderService.parsePort("not-a-number");
        OrderService.parsePort("-1");
    }
}
