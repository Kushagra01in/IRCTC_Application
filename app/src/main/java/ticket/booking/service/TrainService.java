package ticket.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import ticket.booking.entities.Train;
import ticket.booking.util.JsonDatabase;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TrainService {
    private static final String TRAINS_FILE = "trains.json";
    private static final java.nio.file.Path LEGACY_TRAINS = Paths.get("app", "src", "main", "java", "ticket", "booking", "localDB", "trains.json");
    private final List<Train> trains;
    public TrainService() throws IOException {
        List<Train> loaded = JsonDatabase.load(TRAINS_FILE, LEGACY_TRAINS, new TypeReference<List<Train>>() { });
        trains = loaded == null ? new ArrayList<Train>() : loaded;
    }
    public List<Train> searchTrains(String source, String destination) {
        List<Train> matches = new ArrayList<Train>();
        for (Train train : trains) {
            int from = indexOfStation(train, source), to = indexOfStation(train, destination);
            if (from >= 0 && to > from) matches.add(train);
        }
        return matches;
    }
    public Train findById(String trainId) {
        for (Train train : trains) if (train.getTrainId().equalsIgnoreCase(trainId)) return train;
        return null;
    }
    public void save() throws IOException { JsonDatabase.save(TRAINS_FILE, trains); }
    private int indexOfStation(Train train, String station) {
        if (station == null || train.getStations() == null) return -1;
        for (int i = 0; i < train.getStations().size(); i++)
            if (station.trim().equalsIgnoreCase(train.getStations().get(i))) return i;
        return -1;
    }
}
