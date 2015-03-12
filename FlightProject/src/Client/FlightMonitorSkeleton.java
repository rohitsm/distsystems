package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Entity.RemoteObjectInterface;
import Marshaller.DataMarshaller;
import Server.FlightImplementation;
import Server.RequestHistory;

public class FlightMonitorSkeleton implements RemoteObjectInterface{
	private DatagramSocket socket;
	private static String name = "FlightMonitor";
	private FlightMonitorImplementation flightMonitor;
	private DataMarshaller marshaller;
	
	public FlightMonitorSkeleton(int port, FlightMonitorImplementation flightMonitor) throws SocketException{
		this(new DatagramSocket(port), flightMonitor);
	}
	
	public FlightMonitorSkeleton(DatagramSocket socket, FlightMonitorImplementation flightMonitor){
		this.socket = socket;
		this.flightMonitor = flightMonitor;
	}
	
	public void listen(long msec) throws IOException{
		long endTime = System.currentTimeMillis() + msec;
		socket.setSoTimeout((int) msec);
		while(true){
			try{
				listen();
				if(System.currentTimeMillis() < endTime){
					socket.setSoTimeout((int)(endTime-System.currentTimeMillis()));
				}else{
					break;
				}
			}catch(SocketTimeoutException e){
				break;
			}
		}
	}
	
	public void listen() throws IOException{
		byte[] buffer = new byte[65536];
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        socket.receive(incoming);
        byte[] result = processMessage(incoming);
        if(result!=null){
        	DatagramPacket packet = new DatagramPacket(result , result.length , incoming.getAddress() , incoming.getPort());
        	socket.send(packet);
        }
	}
	
	public String getName(){
		return name;
	}

	@Override
	public byte[] processMessage(DatagramPacket packet) {
		// TODO Auto-generated method stub
		byte[] message = packet.getData();
		int position = 0;
		int length = message[position];
		position++;
		String className = new String(message, position, length);
		position+=length;
		
		length = message[position];
		position++;
		String methodName = new String(message, position, length);
		position+=length;
		
		length = message[position];
		position++;
		int messageNo = Integer.parseInt(new String(message, position, length));
		position+=length;
		
		byte[] sourceAddress = packet.getAddress().getAddress();
		
		byte[] data = marshaller.subBytes(message, position, packet.getLength());
		if(methodName.equals("update")){
			int availableSeats = (Integer)marshaller.fromMessage(data);
			flightMonitor.update(availableSeats);
			return marshaller.nullMessage();
		}
		return null;
	}
}
