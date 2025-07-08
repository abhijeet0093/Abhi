
import java.sql.*;
import java.util.Scanner;

class Room {

    int roomNumber;
    boolean isBooked;
    String customerName;

    Room(int roomNumber, boolean isBooked, String customerName) {
        this.roomNumber = roomNumber;
        this.isBooked = isBooked;
        this.customerName = customerName;
    }

    public void displayStatus() {
        if (isBooked) {
            System.out.println("Room " + roomNumber + ": Booked by " + customerName); 
        }else {
            System.out.println("Room " + roomNumber + ": Vacant");
        }
    }
}

class BookingThread extends Thread {

    private final Connection conn;
    private final int roomNumber;
    private final String customerName;

    BookingThread(Connection conn, int roomNumber, String customerName) {
        this.conn = conn;
        this.roomNumber = roomNumber;
        this.customerName = customerName;
    }

    public void run() {
        try {
            String checkSql = "SELECT is_booked FROM rooms WHERE room_number=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, roomNumber);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                boolean isBooked = rs.getBoolean("is_booked");
                if (!isBooked) {
                    Thread.sleep(500); // simulate delay
                    String updateSql = "UPDATE rooms SET is_booked=true, customer_name=? WHERE room_number=?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setString(1, customerName);
                    updateStmt.setInt(2, roomNumber);
                    updateStmt.executeUpdate();
                    System.out.println("Room " + roomNumber + " successfully booked by " + customerName);
                } else {
                    System.out.println("Room " + roomNumber + " is already booked.");
                }
            }
        } catch (Exception e) {
            System.out.println("Booking error: " + e.getMessage());
        }
    }
}

public class HotelManagement {

    static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_db";
    static final String DB_USER = "root";
    static final String DB_PASS = "yourpassword"; // change this

    static Scanner scanner = new Scanner(System.in);
    static String userRole = "";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            if (!login()) {
                return;
            }

            int choice;
            do {
                printMenu();
                choice = scanner.nextInt();
                scanner.nextLine(); // flush

                switch (choice) {
                    case 1 ->
                        viewRooms(conn);
                    case 2 ->
                        handleMultiThreadBooking(conn);
                    case 3 -> {
                        if (userRole.equals("admin")) {
                            vacateRoom(conn); 
                        }else {
                            exitProgram();
                        }
                    }
                    case 4 -> {
                        if (userRole.equals("admin")) {
                            exitProgram(); 
                        }else {
                            System.out.println("Invalid option.");
                        }
                    }
                    default ->
                        System.out.println("Invalid choice.");
                }

            } while (true);

        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    static boolean login() {
        int attempts = 3;
        while (attempts-- > 0) {
            System.out.print("Username (admin/guest): ");
            String user = scanner.next();
            System.out.print("Password: ");
            String pass = scanner.next();

            if (user.equals("admin") && pass.equals("admin123")) {
                userRole = "admin";
                return true;
            } else if (user.equals("guest") && pass.equals("guest123")) {
                userRole = "guest";
                return true;
            } else {
                System.out.println("Invalid login. Attempts left: " + attempts);
            }
        }
        return false;
    }

    static void printMenu() {
        System.out.println("\n--- Hotel Management Menu (" + userRole.toUpperCase() + ") ---");
        System.out.println("1. View Rooms");
        System.out.println("2. Book Room (Multi-threaded)");
        if (userRole.equals("admin")) {
            System.out.println("3. Vacate Room");
            System.out.println("4. Exit");
        } else {
            System.out.println("3. Exit");
        }
        System.out.print("Enter choice: ");
    }

    static void viewRooms(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM rooms");

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("room_number"),
                        rs.getBoolean("is_booked"),
                        rs.getString("customer_name")
                );
                room.displayStatus();
            }
        } catch (SQLException e) {
            System.out.println("Error viewing rooms: " + e.getMessage());
        }
    }

    static void handleMultiThreadBooking(Connection conn) {
        System.out.print("Enter room number to book: ");
        int roomNum = scanner.nextInt();
        scanner.nextLine(); // flush
        System.out.print("Enter first customer name: ");
        String name1 = scanner.nextLine();
        System.out.print("Enter second customer name: ");
        String name2 = scanner.nextLine();

        BookingThread t1 = new BookingThread(conn, roomNum, name1);
        BookingThread t2 = new BookingThread(conn, roomNum, name2);
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted.");
        }
    }

    static void vacateRoom(Connection conn) {
        try {
            System.out.print("Enter room number to vacate: ");
            int roomNum = scanner.nextInt();
            PreparedStatement ps = conn.prepareStatement("UPDATE rooms SET is_booked=false, customer_name='' WHERE room_number=?");
            ps.setInt(1, roomNum);
            int updated = ps.executeUpdate();
            if (updated > 0) {
                System.out.println("Room " + roomNum + " vacated successfully."); 
            }else {
                System.out.println("Room not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error vacating room: " + e.getMessage());
        }
    }

    static void exitProgram() {
        System.out.println("Thank you. Exiting...");
        System.exit(0);
    }
}
