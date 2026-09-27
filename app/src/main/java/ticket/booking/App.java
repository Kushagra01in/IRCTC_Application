package ticket.booking;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.service.UserBookingService;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        try { new App().start(); }
        catch (IOException exception) { System.out.println("Unable to open the booking database: " + exception.getMessage()); }
    }
    private void start() throws IOException {
        UserBookingService service = new UserBookingService(); Scanner scanner = new Scanner(System.in);
        System.out.println("Running Train Booking System");
        while (true) {
            menu(service.isLoggedIn()); String option = line(scanner, "Choose option: ");
            if (option == null || "7".equals(option)) { System.out.println("Goodbye."); return; }
            if ("1".equals(option)) signUp(scanner, service);
            else if ("2".equals(option)) login(scanner, service);
            else if ("3".equals(option)) bookings(service);
            else if ("4".equals(option)) searchAndBook(scanner, service);
            else if ("5".equals(option)) cancel(scanner, service);
            else System.out.println("Please choose a number from 1 to 7.");
        }
    }
    private void menu(boolean loggedIn) {
        System.out.println("\n1. Sign up\n2. Login\n3. Fetch bookings\n4. Search trains and book a seat\n5. Cancel a booking\n7. Exit");
        if (!loggedIn) System.out.println("(Log in before booking or cancelling.)");
    }
    private void signUp(Scanner scanner, UserBookingService service) throws IOException {
        System.out.println(service.signUp(line(scanner, "Username: "), line(scanner, "Password: ")) ? "Account created and logged in." : "Username is taken, or the details were empty.");
    }
    private void login(Scanner scanner, UserBookingService service) {
        System.out.println(service.login(line(scanner, "Username: "), line(scanner, "Password: ")) ? "Logged in." : "Invalid username or password.");
    }
    private void bookings(UserBookingService service) {
        if (!service.isLoggedIn()) { System.out.println("Please log in first."); return; }
        List<Ticket> tickets = service.fetchBookings();
        if (tickets.isEmpty()) { System.out.println("You have no bookings."); return; }
        for (Ticket ticket : tickets) System.out.println(ticket.getTicketInfo());
    }
    private void searchAndBook(Scanner scanner, UserBookingService service) throws IOException {
        if (!service.isLoggedIn()) { System.out.println("Please log in first."); return; }
        String source = line(scanner, "Source station: "), destination = line(scanner, "Destination station: ");
        List<Train> trains = service.searchTrains(source, destination);
        if (trains.isEmpty()) { System.out.println("No trains found for that route."); return; }
        for (int i = 0; i < trains.size(); i++) System.out.println((i + 1) + ". " + trains.get(i).getTrainInfo());
        Integer selection = number(scanner, "Choose a train number: ");
        if (selection == null || selection < 1 || selection > trains.size()) { System.out.println("Invalid train selection."); return; }
        Train train = trains.get(selection - 1); printSeats(train);
        Integer row = number(scanner, "Row number: "), seat = number(scanner, "Seat number: ");
        if (row == null || seat == null) { System.out.println("Row and seat must be numbers."); return; }
        System.out.println(service.bookSeat(train.getTrainId(), row - 1, seat - 1, source, destination) ? "Seat booked successfully." : "That seat is unavailable or invalid.");
    }
    private void cancel(Scanner scanner, UserBookingService service) throws IOException {
        if (!service.isLoggedIn()) { System.out.println("Please log in first."); return; }
        System.out.println(service.cancelBooking(line(scanner, "Ticket ID to cancel: ")) ? "Booking cancelled." : "Ticket not found.");
    }
    private void printSeats(Train train) { System.out.println("0 = available, 1 = booked"); for (int row = 0; row < train.getSeats().size(); row++) System.out.println("Row " + (row + 1) + ": " + train.getSeats().get(row)); }
    private String line(Scanner scanner, String prompt) { System.out.print(prompt); return scanner.hasNextLine() ? scanner.nextLine().trim() : null; }
    private Integer number(Scanner scanner, String prompt) { try { return Integer.valueOf(line(scanner, prompt)); } catch (Exception ignored) { return null; } }
}
