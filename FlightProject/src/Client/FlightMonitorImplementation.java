package Client;

import Entity.FlightMonitorInterface;


public class FlightMonitorImplementation implements FlightMonitorInterface{
	private int iD;
	private int availableSeats;
	
	public FlightMonitorImplementation(int iD){
		this.iD = iD;
	}
	
	@Override
	public void update(int availableSeats) {
		// TODO Auto-generated method stub
		if(this.availableSeats != availableSeats){
			System.out.println("Flight "+ iD + " has " + availableSeats + " seats remaining. ");
			this.availableSeats = availableSeats;
		}
	}
}
