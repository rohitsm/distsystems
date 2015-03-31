package Entity;
import java.util.List;

//this interface contains the methods that can be remotely called from the client
public interface FlightInterface {
	//returns a list of flight IDs matching, given flight source and destination
	public List<Integer> getID(String source, String destination);
	//returns flight details, given flight ID
	public FlightDetails getFlightDetails(int iD);
	//returns result status of seat booking, even the flight ID and number of seats to book
	public int bookFlight(int iD, int seats);
	//returns if monitor method is successful, given flight ID and duration to monitor
	public boolean monitorFlight(int iD, long msec);
	
	//additional functions
	//login to flight system
	public boolean login(String user, String password);
	//return number of tickets booked by user
	public int viewTickets(int iD);
	//cancels booking of user
	public boolean cancelTickets(int iD, int tickets);
}