package Entity;

public class FlightDetails {
	private long time;
	private float airfare;
	private int availableSeats;
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
}
