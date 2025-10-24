import java.sql.*;
import java.util.Scanner;

public class SmartCarParkingSystem {
    
    static final String URL = "jdbc:mysql://localhost:3306/smart_parking_db";
    static final String USER = "root"; 
    static final String PASSWORD = "root";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to Smart Car Parking Database!");

            while (true) {
                System.out.println("\n=== SMART CAR PARKING SYSTEM ===");
                System.out.println("1. View Available Slots");
                System.out.println("2. Park a Vehicle");
                System.out.println("3. Exit a Vehicle");
                System.out.println("4. View All Slots");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        viewAvailableSlots(con);
                        break;
                    case 2:
                        System.out.print("Enter Slot Number: ");
                        String slotNum = sc.nextLine();
                        System.out.print("Enter Vehicle Number: ");
                        String vehicleNum = sc.nextLine();
                        parkVehicle(con, slotNum, vehicleNum);
                        break;
                    case 3:
                        System.out.print("Enter Slot Number to Exit Vehicle: ");
                        String exitSlot = sc.nextLine();
                        exitVehicle(con, exitSlot);
                        break;
                    case 4:
                        viewAllSlots(con);
                        break;
                    case 5:
                        System.out.println("Exiting... Thank you!");
                        con.close();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to view available slots
    public static void viewAvailableSlots(Connection con) throws SQLException {
        String query = "SELECT * FROM parking_slots WHERE status='Available'";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        System.out.println("\nAvailable Slots:");
        while (rs.next()) {
            System.out.println("Slot: " + rs.getString("slot_number"));
        }
    }

    // Function to park vehicle
    public static void parkVehicle(Connection con, String slotNum, String vehicleNum) throws SQLException {
        String query = "UPDATE parking_slots SET status='Occupied', vehicle_number=?, entry_time=NOW() WHERE slot_number=? AND status='Available'";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, vehicleNum);
        ps.setString(2, slotNum);
        int rows = ps.executeUpdate();

        if (rows > 0)
            System.out.println("Vehicle parked successfully in slot " + slotNum);
        else
            System.out.println("Slot not available or already occupied!");
    }

    // Function to exit vehicle
    public static void exitVehicle(Connection con, String slotNum) throws SQLException {
        String query = "UPDATE parking_slots SET status='Available', vehicle_number=NULL, exit_time=NOW() WHERE slot_number=? AND status='Occupied'";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, slotNum);
        int rows = ps.executeUpdate();

        if (rows > 0)
            System.out.println("Vehicle exited successfully from slot " + slotNum);
        else
            System.out.println("No vehicle found in this slot!");
    }

    // Function to view all slots
    public static void viewAllSlots(Connection con) throws SQLException {
        String query = "SELECT * FROM parking_slots";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        System.out.println("\nAll Parking Slots:");
        while (rs.next()) {
            System.out.println("Slot: " + rs.getString("slot_number") + 
                               " | Status: " + rs.getString("status") + 
                               " | Vehicle: " + rs.getString("vehicle_number"));
        }
    }
}
