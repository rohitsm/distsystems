package Client;

import Entity.FlightMonitorInterface;

//implementation of flight monitor
public class FlightMonitorImplementation implements FlightMonitorInterface{
	//flight ID
	private int iD;
	//last notified available seats
	private int availableSeats;
	
	public FlightMonitorImplementation(int iD){
		this.iD = iD;
	}
	
	//update the number of available seats
	@Override
	public void update(int availableSeats) {
		// TODO Auto-generated method stub
		//if number of available seat changes
		if(this.availableSeats != availableSeats){
			//print new current available seats
			System.out.println("Flight "+ iD + " has " + availableSeats + " seats remaining. ");
			//update last notified available seats
			this.availableSeats = availableSeats;
		}
	}
}
