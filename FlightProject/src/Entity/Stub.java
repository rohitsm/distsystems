package Entity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import Marshaller.DataMarshaller;

public abstract class Stub{
	protected DatagramSocket socket;
	private InetAddress host;
	private int port;
	protected DataMarshaller marshaller;
	private int packetCounter;
	
	public Stub(DatagramSocket socket, InetAddress host, int port){
		this.socket = socket;
		this.host = host;
		this.port = port;
		marshaller = new DataMarshaller();
		packetCounter = 0;
	}
	
	public abstract String getName();
	
	private byte[] send(byte[] message) throws IOException{
		socket.setSoTimeout(1000);
		DatagramPacket packet = new DatagramPacket(message , message.length , host , port);
		socket.send(packet);
		byte[] buffer = new byte[65536];
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		socket.receive(reply);
		return marshaller.subBytes(buffer, 0, reply.getLength());
	}
	
	protected byte[] sendUntil(byte[] message, int count) throws IOException{
		int counter = 0;
		while(true){
			if(counter >= count)
				throw new SocketTimeoutException();
			try{
				return send(message);
			}catch(SocketTimeoutException e){
				
			}
			counter++;
		}
	}
	
	protected byte[] createPacketHeader(String methodName){
		String counter = Integer.toString(packetCounter);
		String header = ((char)getName().length()) + getName() +
				((char)methodName.length()) + methodName +
				((char)counter.length()) + counter;
		packetCounter++;
		return header.getBytes();
	}
}
