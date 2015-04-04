package Server;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Entity.FlightDetails;
import Entity.FlightInterface;
import Entity.FlightMonitorInterface;

//flight implementation for flight application
public class FlightImplementation implements FlightInterface{
	//maps flight id to flights
	private Map<Integer, Flight> flights;
	//flight id that monitor request has accepted
	private Integer toBeMonitoredFlight;
	private long monitorPeriod;
	//maps flight id to monitor objects
	private Map<Integer, Set<FlightMonitorInterface>> monitors;
	//maps monitor object to its expiry
	private Map<FlightMonitorInterface, Long> monitorEndTime;
	
	public FlightImplementation(){
		flights = new HashMap();
		toBeMonitoredFlight = null;
		monitors = new HashMap();
		monitorEndTime = new HashMap();
		loadUsers();
	}
	
	public FlightImplementation(Map<Integer, Flight> flights){
		this.flights = flights;
	}
	
	//add a flight to collection
	public void addFlight(int iD, Flight flight){
		flights.put(iD, flight);
	}
	
	//remove a flight from collection
	public void removeFlight(int iD){
		flights.remove(iD);
	}
	
	//returns a list of flight IDs matching, given flight source and destination
	@Override
	public List<Integer> getID(String source, String destination) {
		// TODO Auto-generated method stub
		//looks through flight collection
		List<Integer> matchingFlights = new ArrayList();
		for(Flight flight:flights.values()){
			//find flights with same specified source and destination
			if(flight.getSource().equals(source) && flight.getDestination().equals(destination)){
				matchingFlights.add(flight.getiD());
			}
		}
		//return IDs of matching flights
		return matchingFlights;
	}

	//returns flight details, given flight ID
	@Override
	public FlightDetails getFlightDetails(int iD){
		// TODO Auto-generated method stub
		//if flight does exist in collection
		if(!flights.containsKey(iD)){
			//return null
			return null;
		}
		//else
		//get flight details
		Flight flight = flights.get(iD);
		FlightDetails details = new FlightDetails();
		details.setTime(flight.getTime());
		details.setAirfare(flight.getAirfare());
		details.setAvailableSeats(flight.getAvailableSeats());
		//return flight details
		return details;
	}

	//returns result status of seat booking, even the flight ID and number of seats to book
	@Override
	public int bookFlight(int iD, int seats) {
		// TODO Auto-generated method stub
		//if flight not found
		if(!flights.containsKey(iD)){
			//return -1
			return -1;
		}
		//if flight has sufficient seats
		Flight flight = flights.get(iD);
		if(flight.getAvailableSeats()>=seats){
			//for addition feature
			if(!recordSeatBooking(iD, seats))
				return -2;
			
			//book seats
			changeFlightSeat(flight, flight.getAvailableSeats() - seats);
			//return 1
			return 1;
		}
		//else
		//return 0
		return 0;
	}
	
	//wrapper method for changing of available seats
	//informs respective monitor when number of seats change
	private void changeFlightSeat(Flight flight, int seats){
		flight.setAvailableSeats(seats);
		informMonitor(flight.getiD(), seats);
	}

	//returns if monitor method is successful, given flight ID and duration to monitor
	@Override
	public boolean monitorFlight(int iD, long msec) {
		// TODO Auto-generated method stub
		//if flight not found
		if(!flights.containsKey(iD)){
			//return false
			return false;
		}
		//else
		//initialized to monitor flight ID
		toBeMonitoredFlight = iD;
		//set monitor period;
		monitorPeriod = msec;
		//return true
		return true;
	}
	
	//adds monitor stub (requires monitor flight function to be executed first)
	public void addMonitor(FlightMonitorInterface monitor){
		//if no flight to be monitored
		if(toBeMonitoredFlight == null)
			//return
			return;
		//else
		//retrieve monitor parameters
		int iD = toBeMonitoredFlight;
		long msec = monitorPeriod;
		//clear flight to be monitored
		toBeMonitoredFlight = null;
		//create empty monitor set for flight if set not initialized
		if(!monitors.containsKey(iD)){
			monitors.put(iD, new HashSet());
		}
		//add monitor to monitor set
		monitors.get(iD).add(monitor);
		//set end time for monitor
		monitorEndTime.put(monitor, System.currentTimeMillis()+msec);
	}
	
	//update respective monitors on changes in flight seat vacancy
	private void informMonitor(Integer iD, int seats){
		//if no monitor for specified flight ID
		if(!monitors.containsKey(iD)){
			//do nothing
			return;
		}
		//else
		List<FlightMonitorInterface> tempList = new ArrayList(monitors.get(iD));
		//loop through monitors for specified flight ID
		for(FlightMonitorInterface monitor:tempList){
			//if monitor has not expire
			if(monitorEndTime.get(monitor) > System.currentTimeMillis()){
				//update monitor
				monitor.update(seats);
			//else
			}else{
				//destroy monitor
				monitors.get(iD).remove(monitor);
				monitorEndTime.remove(monitor);
			}
		}
	}

	//====================================for additional features=========================================
	//maps users to passwords
	private Map<String, String> userAccounts;
	//maps ip addresses to users
	private Map<InetAddress, String> userIPs;
	//user of current request message
	private String currentUser;
	//maps user to their booking record
	private Map<String, Map<Integer, Integer>> userBookings;
	
	//dummy data for user login
	private void loadUsers(){
		userAccounts = new HashMap();
		userAccounts.put("user1", "123");
		userAccounts.put("user2", "abc");
		//initialize additional feature attributes
		userIPs = new HashMap();
		currentUser = "";
		userBookings = new HashMap();
	}
	
	//additional functions
	//login to flight system
	@Override
	public boolean login(String user, String password) {
		// TODO Auto-generated method stub
		//if user exist and password matches user
		if(userAccounts.containsKey(user) && userAccounts.get(user).equals(password)){
			//set current user to user
			currentUser = user;
			//return true
			return true;
		}
		return false;
	}
	
	//registers an ip address to the user
	public void registerUser(InetAddress address, String user){
		//confirms the current user is login
		if(currentUser.equals(user)){
			//map ip address to user
			userIPs.put(address, user);
		}
	}
	
	//sets current user based on ip address
	public void setUser(InetAddress address){
		//if ip address maps to a user 
		if(userIPs.containsKey(address)){
			//set current user to that user
			currentUser = userIPs.get(address);
		}else{
			//else, clear current user
			currentUser = "";
		}
	}
	
	//store booking record in user booking record
	private boolean recordSeatBooking(int iD, int seats){
		if(userAccounts.containsKey(currentUser)){
			int ticketsBooked = viewTickets(iD);
			if(ticketsBooked == 0){
				if(!userBookings.containsKey(currentUser))
					userBookings.put(currentUser, new HashMap());
			}
			userBookings.get(currentUser).put(iD, ticketsBooked+seats);
			return true;
		}
		return false;
		
	}
	
	//return number of tickets booked by user
	@Override
	public int viewTickets(int iD) {
		// TODO Auto-generated method stub
		//find booking record
		if(userBookings.containsKey(currentUser)){
			if(userBookings.get(currentUser).containsKey(iD)){
				//if exist, return record
				return userBookings.get(currentUser).get(iD);
			}
		}
		return 0;
	}

	//cancels booking of user
	@Override
	public boolean cancelTickets(int iD, int tickets) {
		// TODO Auto-generated method stub
		int ticketsBooked = viewTickets(iD);
		//if booked tickets 0, no tickets to cancel
		if(ticketsBooked == 0)
			return false;
		//if booked tickets equal cancel tickets
		if(tickets == ticketsBooked){
			//remove booking record for flight ID
			userBookings.get(currentUser).remove(iD);
		//if booked tickets is great than cancel tickets
		}else if(tickets < ticketsBooked){
			//set tickets booked to new value
			userBookings.get(currentUser).put(iD, ticketsBooked - tickets);
		}else{
			//if cancel tickets greater than booked tickets, cancellation fail
			return false;
		}
		//update available seats for flight
		Flight flight = flights.get(iD);
		changeFlightSeat(flight, flight.getAvailableSeats() + tickets);
		return true;
	}
}
