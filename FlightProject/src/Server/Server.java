package Server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import Entity.Skeleton;
import Marshaller.DataMarshaller;

//server class in charge of directing incoming packets to a registered object
public class Server {
	//socket to send/receive messages
	private DatagramSocket socket;
	//registry maps object name to a remote objects
	private Map<String, Skeleton> registry;
	
	public Server(int port) throws SocketException{
		socket = new DatagramSocket(port);
		registry = new HashMap();
	}
	
	//waits for incoming message and forwards them to the respective object
	public void listen() throws IOException{
		//buffer to receive message
		byte[] buffer = new byte[65536];
		//create receive packet
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        //wait for incoming packet
        socket.receive(incoming);
        //get length of object name
        int nameLength = buffer[0];
        //check if length of object name is valid
        if(incoming.getLength() < (nameLength+1))
        	return;
        //get object name
        String name = new String(buffer, 1, nameLength);
        //check if object name is valid
        if(!registry.containsKey(name))
        	return;
        //retrieve object from registry
        //forwards message to retrieved object
        byte[] result = registry.get(name).processMessage(incoming);
        //if there is reply message
        if(result!=null){
        	//send reply message back to client
        	DatagramPacket packet = new DatagramPacket(result , result.length , incoming.getAddress() , incoming.getPort());
        	socket.send(packet);
        }
	}
	
	//registers a remote object with an object name
	public void register(Skeleton remoteObject, String name){
		registry.put(name, remoteObject);
	}
	
	//deregisters a remote object based on the object name
	public void unregister(String name){
		registry.remove(name);
	}
}
