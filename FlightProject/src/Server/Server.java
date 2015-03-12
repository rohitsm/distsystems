package Server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import Entity.RemoteObjectInterface;
import Marshaller.DataMarshaller;


public class Server {
	private DatagramSocket socket;
	private Map<String, RemoteObjectInterface> registry;
	
	public Server(int port) throws SocketException{
		socket = new DatagramSocket(port);
		registry = new HashMap();
	}
	
	public void listen() throws IOException{
		byte[] buffer = new byte[65536];
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        socket.receive(incoming);
        int nameLength = buffer[0];
        String name = new String(buffer, 1, nameLength);
        byte[] result = registry.get(name).processMessage(incoming);
        if(result!=null){
        	DatagramPacket packet = new DatagramPacket(result , result.length , incoming.getAddress() , incoming.getPort());
        	socket.send(packet);
        }
	}
	
	public void register(RemoteObjectInterface remoteObject, String name){
		registry.put(name, remoteObject);
	}
	
	public void unregister(String name){
		registry.remove(name);
	}
	
	public static void main(String[] args){
		if(args.length < 1){
			args = new String[1];
			args[0] = "5000";
		}
		int port = Integer.parseInt(args[0]);
		FlightImplementation flights = new FlightImplementation();
		flights.addFlight(1, new Flight(1, "a", "b", 1000, (float)2.40, 30));
		FlightSkeleton skeleton = new FlightSkeleton(flights);
		try {
			Server server = new Server(port);
			server.register(skeleton, skeleton.getName());
			while(true){
				server.listen();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
