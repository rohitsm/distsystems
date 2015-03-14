package Entity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import Marshaller.DataMarshaller;

//this abstract class contains methods common for a stub object
//roles of stub
//-initiate connection with server JVM
//-marshalling and sending of parameters
//-wait for results
//-unmarshalling of results
//-return results to caller

public abstract class Stub{
	//socket to send messages
	protected DatagramSocket socket;
	//host ip and port
	private InetAddress host;
	private int port;
	//data marshaller object
	protected DataMarshaller marshaller;
	//counter to keep track of repeated messages
	private int packetCounter;
	
	public Stub(DatagramSocket socket, InetAddress host, int port){
		this.socket = socket;
		this.host = host;
		this.port = port;
		marshaller = new DataMarshaller();
		packetCounter = 0;
	}
	
	//retrieves (class) name of object
	public abstract String getName();
	
	//sends a message and wait for 1 second for reply
	private byte[] send(byte[] message) throws IOException{
		//sets time out to 1000 ms
		socket.setSoTimeout(1000);
		//place message in packet
		DatagramPacket packet = new DatagramPacket(message , message.length , host , port);
		//send packet
		socket.send(packet);
		//create receive buffer
		byte[] buffer = new byte[65536];
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		//wait for reply message
		socket.receive(reply);
		//return reply message
		return marshaller.subBytes(buffer, 0, reply.getLength());
	}
	
	//sends a message until a specified number timeouts has occurred
	protected byte[] sendUntil(byte[] message, int count) throws IOException{
		int counter = 0;
		while(true){
			//if timeout threshold reached
			if(counter >= count)
				//throw time out exception
				throw new SocketTimeoutException();
			try{
				//resend message
				return send(message);
			}catch(SocketTimeoutException e){
				//increase counter by 1 for each time out exception
				counter++;
			}
		}
	}
	
	//create a byte array contain header info
	//header info consist of object name, method name, packet counter
	protected byte[] createPacketHeader(String methodName){
		//format (where X is the length of the item):
		//-length of object name (1 byte)
		//-object name (X byte)
		//-length of method name (1 byte)
		//-method name (X byte)
		//-length of packet counter string (1 byte)
		//-packet counter (X byte)
		String counter = Integer.toString(packetCounter);
		String header = ((char)getName().length()) + getName() +
				((char)methodName.length()) + methodName +
				((char)counter.length()) + counter;
		//packet counter is automatically increase for each header generation
		packetCounter++;
		return header.getBytes();
	}
}
