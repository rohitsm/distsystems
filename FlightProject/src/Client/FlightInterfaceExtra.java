package Client;

import Entity.FlightInterface;

public interface FlightInterfaceExtra extends FlightInterface{
	//idempotent
	public String findRoutes(String source, String destination);
	
	//non-idempotent
	public String cancelFlight(int regId);
}
