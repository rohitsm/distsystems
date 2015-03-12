package Server;

import java.io.IOException;

//server application for flight project
public class FlightServerApplication {
	public static void main(String[] args){
		//if no parameter provided
		if(args.length < 1){
			//initialize parameter to default
			args = new String[1];
			args[0] = "5000";
		}
		//convert string to port number
		int port = Integer.parseInt(args[0]);
		//create flight implementation
		FlightImplementation flights = new FlightImplementation();
		//add dummy data
		flights.addFlight(1, new Flight(1, "a", "b", 1000, (float)2.40, 30));
		//create flight skeleton
		FlightSkeleton skeleton = new FlightSkeleton(flights);
		try {
			//create server
			Server server = new Server(port);
			//register object on server
			server.register(skeleton, skeleton.getName());
			//listen indefinitely
			while(true){
				server.listen();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
