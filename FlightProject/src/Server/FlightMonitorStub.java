package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import Entity.FlightMonitorInterface;
import Entity.Stub;
import Marshaller.DataMarshaller;

public class FlightMonitorStub extends Stub implements FlightMonitorInterface{
	private String name = "FlightMonitor";
	
	public FlightMonitorStub(DatagramSocket socket, InetAddress host, int port){
		super(socket, host, port);
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public void update(int availableSeats) {		
		// TODO Auto-generated method stub
		byte[] message = createPacketHeader("update");
		message = marshaller.appendBytes(message, marshaller.toMessage(availableSeats));
		try{
			message = sendUntil(message, 5*60);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
