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
		//create header
		byte[] message = createPacketHeader("getID");
		//append marshaled parameters
		message = marshaller.appendBytes(message, marshaller.toMessage(source));
		message = marshaller.appendBytes(message, marshaller.toMessage(destination));
		try{
			//send message for 300 attempts (5 minutes)
			message = sendUntil(message, 5*60);
			//return unmarshaled results
			return (List<Integer>)marshaller.fromMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//returns flight details, given flight ID
	@Override
	public FlightDetails getFlightDetails(int iD) {
		// TODO Auto-generated method stub
		//create header
		byte[] message = createPacketHeader("getFlightDetails");
		//append marshaled parameters
		message = marshaller.appendBytes(message, marshaller.toMessage(iD));
		try{
			//send message for 300 attempts (5 minutes)
			message = sendUntil(message, 5*60);
			//return unmarshaled results
			return (FlightDetails)marshaller.fromMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//returns result status of seat booking, even the flight ID and number of seats to book
	@Override
	public int bookFlight(int iD, int seats) {
		// TODO Auto-generated method stub
		//create header
		byte[] message =  createPacketHeader("bookFlight");
		//append marshaled parameters
		message = marshaller.appendBytes(message, marshaller.toMessage(iD));
		message = marshaller.appendBytes(message, marshaller.toMessage(seats));
		try{
			//send message for 300 attempts (5 minutes)
			message = sendUntil(message, 5*60);
			//return unmarshaled results
			return (int)marshaller.fromMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -2;
	}

	//returns if monitor method is successful, given flight ID and duration to monitor
	@Override
	public boolean monitorFlight(int iD, long msec) {
		// TODO Auto-generated method stub
		//create header
		byte[] message = createPacketHeader("monitorFlight");
		//append marshaled parameters
		message = marshaller.appendBytes(message, marshaller.toMessage(iD));
		message = marshaller.appendBytes(message, marshaller.toMessage(msec));
		try{
			//send message for 300 attempts (5 minutes)
			message = sendUntil(message, 5*60);
			//unmarshaled results
			boolean result = (boolean)marshaller.fromMessage(message);
			//if successful
			if(result){
				//create monitor implementation and skeleton
				FlightMonitorImplementation monitor = new FlightMonitorImplementation(iD);
				FlightMonitorSkeleton server = new FlightMonitorSkeleton(socket, monitor);
				//listen for updates
				server.listen(msec);
			}
			//return unmarshaled results
			return (boolean)marshaller.fromMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
