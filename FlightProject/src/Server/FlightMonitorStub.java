package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import Entity.FlightMonitorInterface;
import Entity.Stub;
import Marshaller.DataMarshaller;

//stub implemention for flight monitoring
public class FlightMonitorStub extends Stub implements FlightMonitorInterface{
	//object(class) name
	private String name = "FlightMonitor";
	
	public FlightMonitorStub(DatagramSocket socket, InetAddress host, int port){
		super(socket, host, port);
	}
	
	public String getName(){
		return name;
	}
	
	//update the number of available seats
	@Override
	public void update(int availableSeats) {		
		// TODO Auto-generated method stub
		//prepare parameters
		byte[] parameters = marshaller.toMessage(availableSeats);
		//send request
		sendRequest("update", parameters, null);
	}

}
