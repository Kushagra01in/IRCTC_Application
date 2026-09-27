package ticket.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.JsonDatabase;
import ticket.booking.util.UserServiceUtil;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserBookingService {
    private static final String USERS_FILE = "users.json";
    private static final java.nio.file.Path LEGACY_USERS = Paths.get("app", "src", "main", "java", "ticket", "booking", "localDB", "users.json");
    private final List<User> users;
    private User currentUser;
    public UserBookingService() throws IOException {
        List<User> loaded = JsonDatabase.load(USERS_FILE, LEGACY_USERS, new TypeReference<List<User>>() { });
        users = loaded == null ? new ArrayList<User>() : loaded;
        for (User user : users) if (user.getTicketsBooked() == null) user.setTicketsBooked(new ArrayList<Ticket>());
    }
    public boolean signUp(String name, String password) throws IOException {
        if (blank(name) || blank(password) || findUser(name) != null) return false;
        currentUser = new User(name.trim(), UserServiceUtil.hashPassword(password), new ArrayList<Ticket>(), UUID.randomUUID().toString());
        users.add(currentUser); JsonDatabase.save(USERS_FILE, users); return true;
    }
    public boolean login(String name, String password) {
        if (blank(name) || blank(password)) return false;
        // Older database data contains duplicate usernames. Check every matching
        // record so a valid password can still access its own saved account.
        for (User user : users) {
            if (name.trim().equalsIgnoreCase(user.getName())
                    && UserServiceUtil.checkPassword(password, user.getHashedPassword())) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }
    public boolean isLoggedIn() { return currentUser != null; }
    public List<Ticket> fetchBookings() { return currentUser == null ? new ArrayList<Ticket>() : currentUser.getTicketsBooked(); }
    public List<Train> searchTrains(String source, String destination) throws IOException { return new TrainService().searchTrains(source, destination); }
    public boolean bookSeat(String trainId, int row, int seat, String source, String destination) throws IOException {
        if (currentUser == null) return false;
        TrainService trains = new TrainService(); Train train = trains.findById(trainId);
        if (train == null || !validSeat(train, row, seat) || train.getSeats().get(row).get(seat) != 0) return false;
        train.getSeats().get(row).set(seat, 1);
        currentUser.getTicketsBooked().add(new Ticket(UUID.randomUUID().toString(), currentUser.getUserId(), source, destination, Instant.now().toString(), train, row, seat));
        trains.save(); JsonDatabase.save(USERS_FILE, users); return true;
    }
    public boolean cancelBooking(String ticketId) throws IOException {
        if (currentUser == null || blank(ticketId)) return false;
        Ticket found = null;
        for (Ticket ticket : currentUser.getTicketsBooked()) if (ticketId.trim().equals(ticket.getTicketId())) { found = ticket; break; }
        if (found == null) return false;
        TrainService trains = new TrainService(); Train train = found.getTrain() == null ? null : trains.findById(found.getTrain().getTrainId());
        if (train != null && validSeat(train, found.getRow(), found.getSeat())) { train.getSeats().get(found.getRow()).set(found.getSeat(), 0); trains.save(); }
        currentUser.getTicketsBooked().remove(found); JsonDatabase.save(USERS_FILE, users); return true;
    }
    private User findUser(String name) { if (blank(name)) return null; for (User user : users) if (name.trim().equalsIgnoreCase(user.getName())) return user; return null; }
    private boolean validSeat(Train train, int row, int seat) { return train.getSeats() != null && row >= 0 && row < train.getSeats().size() && train.getSeats().get(row) != null && seat >= 0 && seat < train.getSeats().get(row).size(); }
    private boolean blank(String value) { return value == null || value.trim().isEmpty(); }
}
