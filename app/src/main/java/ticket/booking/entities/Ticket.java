package ticket.booking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Ticket {
    private String ticketId, userId, source, destination, dateOfTravel;
    private Train train;
    private int row = -1, seat = -1;
    public Ticket() { }
    public Ticket(String ticketId, String userId, String source, String destination, String dateOfTravel, Train train, int row, int seat) { this.ticketId=ticketId; this.userId=userId; this.source=source; this.destination=destination; this.dateOfTravel=dateOfTravel; this.train=train; this.row=row; this.seat=seat; }
    public String getTicketId() { return ticketId; } public String getUserId() { return userId; } public String getSource() { return source; } public String getDestination() { return destination; } public String getDateOfTravel() { return dateOfTravel; } public Train getTrain() { return train; } public int getRow() { return row; } public int getSeat() { return seat; }
    public void setTicketId(String value) { ticketId=value; } public void setUserId(String value) { userId=value; } public void setSource(String value) { source=value; } public void setDestination(String value) { destination=value; } public void setDateOfTravel(String value) { dateOfTravel=value; } public void setTrain(Train value) { train=value; } public void setRow(int value) { row=value; } public void setSeat(int value) { seat=value; }
    public String getTicketInfo() { return String.format("Ticket %s: %s to %s | train %s | seat %d,%d | booked %s", ticketId, source, destination, train == null ? "unknown" : train.getTrainNo(), row + 1, seat + 1, dateOfTravel); }
}
