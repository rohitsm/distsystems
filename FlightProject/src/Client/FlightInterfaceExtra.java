package Client;

import Entity.FlightInterface;

//ideas for addition methods
//addition interface (yet to implemented interface)
public interface FlightInterfaceExtra extends FlightInterface{
	//idempotent
	//given a source and destination return out possible routes
	//e.g. 
	//id: 1 source: a destination: b
	//id: 2 source: b destination: c
	//id: 3 source: a destination: c
	//findRoutes(a, c) will return (1, 2) and 3
	public String findRoutes(String source, String destination);
	
	//idempotent
	//given the registration id, cancal flight seat booking
	public String cancelFlight(int regId);
}
