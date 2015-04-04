package Entity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

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
	protected int packetCounter;
	
	public Stub(DatagramSocket socket, InetAddress host, int port){
		this.socket = socket;
		this.host = host;
		this.port = port;
		marshaller = new DataMarshaller();
		packetCounter = new Random().nextInt();
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
	
	//sends a message for a duration
	protected byte[] sendUntil(byte[] message, long duration) throws IOException{
		long startTime = System.currentTimeMillis();
		while(true){
			//if timeout threshold reached
			if((System.currentTimeMillis()-startTime) > duration)
				//throw time out exception
				throw new SocketTimeoutException();
			try{
				//resend message
				return send(message);
			}catch(SocketTimeoutException e){
				//catch time out exception
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
	
	protected Object sendRequest(String functionName, byte[] parameters, Class expectedClass){
		// TODO Auto-generated method stub
		//create header
		byte[] message = createPacketHeader(functionName);
		//append marshaled parameters
		message = marshaller.appendBytes(message, parameters);
		try{
			//send message for 5 minutes
			long startTime = System.currentTimeMillis();
			long duration = 5*60*1000;
			while(true){
				long remainingTime = duration - (System.currentTimeMillis() - startTime);
				if(remainingTime <= 0)
					break;
				//send message for remaining duration
				byte[] reply = sendUntil(message, remainingTime);
				//a reply is received
				//retrieve message no
				int length = reply[0];
				//if message not long enough, skipped reply
				if(reply.length <= (1+length))
					continue;
				try{
					int messageNo = Integer.parseInt(new String(reply, 1, length));
					//extract data bytes
					byte[] data = marshaller.subBytes(reply, 1+length, reply.length);
					//unmarshal data bytes
					Object unmarshalledData = marshaller.fromMessage(data);
					//if messageNo correct or data type correct, return data
					if(messageNo==packetCounter){
						if(unmarshalledData==null)
							return null;
						if(expectedClass!=null){
							if(expectedClass.isInstance(unmarshalledData))
								return unmarshalledData;
						}
					}	
				}catch(Exception e){
					//if message no is not an integer
					//skip reply
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
