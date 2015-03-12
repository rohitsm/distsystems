package Entity;
import java.util.List;

public interface FlightInterface {
	public List<Integer> getID(String source, String destination);
	public FlightDetails getFlightDetails(int iD);
	public int bookFlight(int iD, int seats);
	public boolean monitorFlight(int iD, long msec);
}