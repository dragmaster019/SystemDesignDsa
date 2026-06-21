import Service.*;

public class Main {

    public static void main(String[] args) {

        UserService   userService   = new UserService();
        DriverService driverService = new DriverService();

        System.out.println("========== Cab Booking Demo ==========\n");

        // ── Scenario 1: Normal ride ─────────────────────────────
        System.out.println("--- Scenario 1: Sarthak books Kolkata → Haldia ---");
        userService.bookRide(1, "Kolkata", "Haldia");
        // Driver 1 (Kolkata, rating 4.8) gets assigned

        driverService.acceptRide(1, 1);
        driverService.completeRide(1, 1);
        // Driver 1 now at Haldia

        userService.viewRideHistory(1);

        System.out.println();

        // ── Scenario 2: User tries to book when already has active ride ─
        System.out.println("--- Scenario 2: Sarthak books again while active ride ---");
        userService.bookRide(1, "Haldia", "Kolkata");
        userService.bookRide(1, "Haldia", "Kolkata"); // should block — active ride exists

        driverService.completeRide(1, 2);

        System.out.println();

        // ── Scenario 3: No driver at location → waiting queue ───
        System.out.println("--- Scenario 3: Shreya books from Durgapur (no driver there) ---");
        userService.bookRide(2, "Durgapur", "Kolkata");
        // No driver at Durgapur → goes to queue

        System.out.println();

        // ── Scenario 4: Driver rejects ride ─────────────────────
        System.out.println("--- Scenario 4: Sarthak books, driver rejects ---");
        userService.bookRide(1, "Haldia", "Kolkata");
        driverService.rejectRide(1, 3); // Driver 1 rejects
        // System tries to find another driver

        System.out.println();

        // ── Scenario 5: Cancel ride ──────────────────────────────
        System.out.println("--- Scenario 5: Sarthak cancels ride ---");
        userService.bookRide(1, "Haldia", "Kolkata");
        userService.cancelRide(1, 4);

        System.out.println();

        // ── View final history ───────────────────────────────────
        System.out.println("--- Final ride history ---");
        userService.viewRideHistory(1);

        System.out.println("\n========== Demo Complete ==========");
    }
}
