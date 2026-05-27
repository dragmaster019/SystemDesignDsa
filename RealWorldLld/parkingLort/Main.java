import Services.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        entryService   entry   = new entryService();
        exitService    exit    = new exitService();
        vehilceService vehicle = new vehilceService();

        System.out.println("========== Parking Lot Demo ==========");
        System.out.println("Total Spots: 2\n");

        // 4 vehicles arrive — only 2 spots available
        entry.parkVehicle("WB12AB1234", "Car");   // gets Spot 1
        entry.parkVehicle("WB14XY5678", "Bike");  // gets Spot 2
        entry.parkVehicle("DL01AB1111", "Car");   // no space → waiting queue
        entry.parkVehicle("MH02CD2222", "Bike");  // no space → waiting queue

        System.out.println();
        vehicle.showParkingStatus();

        Thread.sleep(2000);  // simulate 2 seconds parking time

        // First vehicle exits → DL01AB1111 auto-assigned from queue
        exit.exitVehicle("WB12AB1234");

        System.out.println();
        vehicle.showParkingStatus();

        Thread.sleep(1000);

        // Second vehicle exits → MH02CD2222 auto-assigned from queue
        exit.exitVehicle("WB14XY5678");

        System.out.println();
        vehicle.showParkingStatus();

        // Remaining vehicles exit
        exit.exitVehicle("DL01AB1111");
        exit.exitVehicle("MH02CD2222");

        System.out.println();
        vehicle.showParkingStatus();

        System.out.println("========== Demo Complete ==========");
    }
}
