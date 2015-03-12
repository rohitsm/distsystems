package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Entity.SkeletonInterface;
import Marshaller.DataMarshaller;
import Server.FlightImplementation;
import Server.RequestHistory;

//skeleton of flight monitor (also plays the role of server class)
public class FlightMonitorSkeleton implements SkeletonInterface{
	//socket to send/receive messages
	private DatagramSocket socket;
	//object(class) name
	private static String name = "FlightMonitor";
	//reference to flight monitor implementation object
	private FlightMonitorImplementation flightMonitor;
	//data marshaller
	private DataMarshaller marshaller;
	
	public FlightMonitorSkeleton(int port, FlightMonitorImplementation flightMonitor) throws SocketException{
		this(new DatagramSocket(port), flightMonitor);
	}
	
	public FlightMonitorSkeleton(DatagramSocket socket, FlightMonitorImplementation flightMonitor){
		this.socket = socket;
		this.flightMonitor = flightMonitor;
	}
	
	//listen until monitor period has ended
	public void listen(long msec) throws IOException{
		//records end time
		long endTime = System.currentTimeMillis() + msec;
		//set time out to end time
		socket.setSoTimeout((int) msec);
		//keep listen for messages
		while(true){
			try{
				//wait for incoming message
				listen();
				//if monitor period is not over
				if(System.currentTimeMillis() < endTime){
					//reset time out to remaining monitor time
					socket.setSoTimeout((int)(endTime-System.currentTimeMillis()));
				}else{
					break;
				}
			}catch(SocketTimeoutException e){
				//if time out, stop listening
				break;
			}
		}
	}
	
	//waits for incoming message and forwards them to the respective object
	public void listen() throws IOException{
		//buffer to receive message
		byte[] buffer = new byte[65536];
		//create receive packet
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        //wait for incoming packet
        socket.receive(incoming);
        //forwards message to processing method
        byte[] result = processMessage(incoming);
        //if there is reply message
        if(result!=null){
        	//send reply message back to client
        	DatagramPacket packet = new DatagramPacket(result , result.length , incoming.getAddress() , incoming.getPort());
        	socket.send(packet);
        }
	}
	
	public String getName(){
		return name;
	}

	//takes in a packet returns a reply message in bytes
	@Override
	public byte[] processMessage(DatagramPacket packet) {
		// TODO Auto-generated method stub
		byte[] message = packet.getData();
		
		//buffer off header
		/////////////////////////////////////////////////////////////////////////
		//get object name
		int position = 0;
		int length = message[position];
		position++;
		String className = new String(message, position, length);
		position+=length;
		//get method name
		length = message[position];
		position++;
		String methodName = new String(message, position, length);
		position+=length;
		//get request counter
		length = message[position];
		position++;
		int messageNo = Integer.parseInt(new String(message, position, length));
		position+=length;
		/////////////////////////////////////////////////////////////////////////
		
		byte[] sourceAddress = packet.getAddress().getAddress();
		
		byte[] data = marshaller.subBytes(message, position, packet.getLength());
		//match method name to method
		if(methodName.equals("update")){
			//unmarshall parameters from message
			int availableSeats = (Integer)marshaller.fromMessage(data);
			//pass parameter to method implementation
			flightMonitor.update(availableSeats);
			//return null message
			return marshaller.nullMessage();
		}
		return null;
	}
}
