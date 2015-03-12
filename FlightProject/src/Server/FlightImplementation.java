package Server;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Entity.FlightDetails;
import Entity.FlightInterface;
import Entity.FlightMonitorInterface;

public class FlightImplementation implements FlightInterface{
	private Map<Integer, Flight> flights;
	private Map<Integer, Set<FlightMonitorInterface>> monitors;
	private Map<FlightMonitorInterface, Long> monitorEndTime;
	
	public FlightImplementation(){
		flights = new HashMap();
	}
	
	public FlightImplementation(Map<Integer, Flight> flights){
		this.flights = flights;
	}
	
	public void addFlight(int iD, Flight flight){
		flights.put(iD, flight);
	}
	
	public void removeFlight(int iD){
		flights.remove(iD);
	}
	
	@Override
	public List<Integer> getID(String source, String destination) {
		// TODO Auto-generated method stub
		List<Integer> matchingFlights = new ArrayList();
		for(Flight flight:flights.values()){
			if(flight.getSource().equals(source) && flight.getDestination().equals(destination)){
				matchingFlights.add(flight.getiD());
			}
		}
		return matchingFlights;
	}

	@Override
	public FlightDetails getFlightDetails(int iD){
		// TODO Auto-generated method stub
		if(!flights.containsKey(iD)){
			return null;
		}
		Flight flight = flights.get(iD);
		FlightDetails details = new FlightDetails();
		details.setTime(flight.getTime());
		details.setAirfare(flight.getAirfare());
		details.setAvailableSeats(flight.getAvailableSeats());
		return details;
	}

	@Override
	public int bookFlight(int iD, int seats) {
		// TODO Auto-generated method stub
		if(!flights.containsKey(iD)){
			return -1;
		}
		Flight flight = flights.get(iD);
		if(flight.getAvailableSeats()>=seats){
			changeFlightSeat(flight, flight.getAvailableSeats() - seats);
			return 1;
		}
		return 0;
	}
	
	private void changeFlightSeat(Flight flight, int seats){
		flight.setAvailableSeats(seats);
		informMonitor(flight.getiD(), seats);
	}

	@Override
	public boolean monitorFlight(int iD, long msec) {
		// TODO Auto-generated method stub
		if(!flights.containsKey(iD)){
			return false;
		}
		if(!monitors.containsKey(iD)){
			monitors.put(iD, new HashSet());
		}
		monitors.get(iD).add(null);
		monitorEndTime.put(null, System.currentTimeMillis()+msec);
		return true;
	}
	
	public void informMonitor(Integer iD, int seats){
		if(!monitors.containsKey(iD)){
			return;
		}
		List<FlightMonitorInterface> tempList = new ArrayList(monitors.get(iD));
		for(FlightMonitorInterface monitor:tempList){
			if(monitorEndTime.get(monitor) > System.currentTimeMillis()){
				monitor.update(seats);
			}else{
				monitors.get(iD).remove(monitor);
				monitorEndTime.remove(monitor);
			}
		}
	}
}
