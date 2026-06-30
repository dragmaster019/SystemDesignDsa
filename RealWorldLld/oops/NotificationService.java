// Real-world style example: how a company (e.g. Uber/Swiggy) sends notifications
// via SMS, Email, Push — demonstrating all 4 OOP pillars.

import java.util.*;

// ---------- 1. ABSTRACTION ----------
// We expose WHAT a notification does (send), hide HOW each channel sends it.
abstract class Notification {

    // ---------- 2. ENCAPSULATION ----------
    // Fields are private; only accessible/modifiable through controlled methods.
    private final String recipient;
    private final String message;

    protected Notification(String recipient, String message) {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("recipient cannot be empty");
        }
        this.recipient = recipient;
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    // Abstract method — every subclass MUST define its own delivery mechanism.
    public abstract void send();
}

// ---------- 3. INHERITANCE ----------
// Each channel reuses recipient/message handling from Notification,
// and only adds its own delivery logic.
class SmsNotification extends Notification {

    public SmsNotification(String phoneNumber, String message) {
        super(phoneNumber, message);
    }

    @Override
    public void send() {
        System.out.println("[SMS] Sending text to " + getRecipient() + ": " + getMessage());
    }
}

class EmailNotification extends Notification {

    public EmailNotification(String emailAddress, String message) {
        super(emailAddress, message);
    }

    @Override
    public void send() {
        System.out.println("[EMAIL] Sending email to " + getRecipient() + ": " + getMessage());
    }
}

// INTERFACE: a pure contract, no shared state. Only classes that need
// this specific ability implement it — SMS/Email don't need to.
interface Retryable {
    void retry(int attempts);
}

// PushNotification both EXTENDS Notification (shares recipient/message state)
// AND IMPLEMENTS Retryable (gains an unrelated capability). Java allows only
// one extends, but many implements — that's the practical difference.
class PushNotification extends Notification implements Retryable {

    public PushNotification(String deviceToken, String message) {
        super(deviceToken, message);
    }

    @Override
    public void send() {
        System.out.println("[PUSH] Sending push to device " + getRecipient() + ": " + getMessage());
    }

    @Override
    public void retry(int attempts) {
        System.out.println("[PUSH] Retrying delivery to " + getRecipient() + " (" + attempts + " attempts left)");
    }
}

// ---------- 4. POLYMORPHISM ----------
// NotificationDispatcher works with the base type "Notification".
// It doesn't know or care if it's SMS, Email, or Push — send() does the
// right thing at runtime depending on the actual object (dynamic dispatch).
class NotificationDispatcher {

    private final List<Notification> queue = new ArrayList<>();

    public void enqueue(Notification notification) {
        queue.add(notification);
    }

    public void dispatchAll() {
        for (Notification n : queue) {
            n.send(); // polymorphic call
        }
        queue.clear();
    }
}

public class NotificationService {
    public static void main(String[] args) {
        NotificationDispatcher dispatcher = new NotificationDispatcher();

        dispatcher.enqueue(new SmsNotification("+91-9876543210", "Your OTP is 482931"));
        dispatcher.enqueue(new EmailNotification("user@example.com", "Your order has shipped"));
        dispatcher.enqueue(new PushNotification("device-token-xyz", "Driver is arriving in 2 mins"));

        dispatcher.dispatchAll();
    }
}