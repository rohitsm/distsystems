package Server;
//flight class used by the server to hold flight data
public class Flight {
	private int iD;
	private String source;
	private String destination;
	private long time;
	private float airfare;
	private int availableSeats;
	
	public Flight(int iD, String source, String destination, long time,
			float airfare, int availableSeats) {
		this.iD = iD;
		this.source = source;
		this.destination = destination;
		this.time = time;
		this.airfare = airfare;
		this.availableSeats = availableSeats;
	}

	//getter and setter for attributes
	///////////////////////////////////////////////////
	public int getiD() {
		return iD;
	}

	public void setiD(int iD) {
		this.iD = iD;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

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
