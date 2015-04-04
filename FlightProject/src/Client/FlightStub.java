package Client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import Entity.FlightDetails;
import Entity.FlightInterface;
import Entity.Stub;
import Marshaller.DataMarshaller;
import Marshaller.SimSocket;

//stub class for flight interface
public class FlightStub extends Stub implements FlightInterface{
	//object(class) name
	private String name = "Flight";
	
	public FlightStub(DatagramSocket socket, InetAddress host, int port){
		super(socket, host, port);
	}
	
	public String getName(){
		return name;
	}
	
	//returns a list of flight IDs matching, given flight source and destination
	@Override
	public List<Integer> getID(String source, String destination) {
		// TODO Auto-generated method stub
		//prepare parameters
		byte[] parameters = marshaller.toMessage(source);
		parameters = marshaller.appendBytes(parameters, marshaller.toMessage(destination));
		//send request
		List<Integer> data = (List<Integer>) sendRequest("getID", parameters, List.class);
		if(data==null)
			data = new ArrayList();
		//return result
		return data;
	}

	//returns flight details, given flight ID
	@Override
	public FlightDetails getFlightDetails(int iD) {
		// TODO Auto-generated method stub
		//prepare parameters
		byte[] parameters = marshaller.toMessage(iD);
		//send request
		FlightDetails data = (FlightDetails) sendRequest("getFlightDetails", parameters, FlightDetails.class);
		//return result
		return data;
	}

	//returns result status of seat booking, even the flight ID and number of seats to book
	@Override
	public int bookFlight(int iD, int seats) {
		// TODO Auto-generated method stub
		//prepare parameters
		byte[] parameters = marshaller.toMessage(iD);
		parameters = marshaller.appendBytes(parameters, marshaller.toMessage(seats));
		//send request
		Integer data = (Integer) sendRequest("bookFlight", parameters, Integer.class);
		if(data==null)
			data = -2;
		//return result
		return data;
	}

	//returns if monitor method is successful, given flight ID and duration to monitor
	@Override
	public boolean monitorFlight(int iD, long msec) {
		// TODO Auto-generated method stub
		//prepare parameters
		byte[] parameters = marshaller.toMessage(iD);
		parameters = marshaller.appendBytes(parameters, marshaller.toMessage(msec));
		//send request
		Boolean data = (Boolean) sendRequest("monitorFlight", parameters, Boolean.class);
		if(data==null)
			data = false;
		//if successful
		if(data){
			//create monitor implementation and skeleton
			FlightMonitorImplementation monitor = new FlightMonitorImplementation(iD);
			FlightMonitorSkeleton server = new FlightMonitorSkeleton(socket, monitor);
			//listen for updates
			try {
				server.listen(msec);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//return result
		return data;
	}

	//additional functions
	//login to flight system
	@Override
	public boolean login(String user, String password) {
		// TODO Auto-generated method stub
		//prepare parameters
		byte[] parameters = marshaller.toMessage(user);
		parameters = marshaller.appendBytes(parameters, marshaller.toMessage(password));
		//send request
		Boolean data = (Boolean) sendRequest("login", parameters, Boolean.class);
		if(data==null)
			data = false;
		//return result
		return data;
	}

	//return number of tickets booked by user
	@Override
	public int viewTickets(int iD) {
		// TODO Auto-generated method stub
		//prepare parameters
		byte[] parameters = marshaller.toMessage(iD);
		//send request
		Integer data = (Integer) sendRequest("viewTickets", parameters, Integer.class);
		if(data == null){
			data = -1;
		}
		//return result
		return data;
	}

	//cancels booking of user
	@Override
	public boolean cancelTickets(int iD, int tickets) {
		// TODO Auto-generated method stub
		//prepare parameters
		byte[] parameters = marshaller.toMessage(iD);
		parameters = marshaller.appendBytes(parameters, marshaller.toMessage(tickets));
		//send request
		Boolean data = (Boolean) sendRequest("cancelTickets", parameters, Boolean.class);
		if(data==null)
			data = false;
		//return result
		return data;
	}
}
