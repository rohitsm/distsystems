package Entity;

//flight details is used similarly to a data structure for easier marshaling and passing of the following fields
public class FlightDetails {
	//departure time of flight
	private long time;
	//airfare of flight
	private float airfare;
	//number of vacant seats remaining for flight
	private int availableSeats;
	
	//getter and setter of attributes
	///////////////////////////////////////////////////
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public float getAirfare() {
		return airfare;
	}
	public void setAirfare(float airfare) {
		this.airfare = airfare;
	}
	public int getAvailableSeats() {
		return availableSeats;
	}
	public void setAvailableSeats(int availableSeats) {
		this.availableSeats = availableSeats;
	}
	///////////////////////////////////////////////////
}
