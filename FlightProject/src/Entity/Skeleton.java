package Entity;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Map;

import Marshaller.DataMarshaller;

//this interface is for the skeleton object
//roles of skeleton
//-unmarshalling of parameters
//-invoking method on actual implemented object
//-marshalling and sending of results

public abstract class Skeleton {
	//data marshaller
	protected DataMarshaller marshaller;
	//function map - maps function name to function handler
	protected Map<String, SkeletonFunctionInterface> functionMap;
	
	//takes in a packet returns a reply message in bytes
	public byte[] processMessage(DatagramPacket packet){
		// TODO Auto-generated method stub
		byte[] message = packet.getData();
		
		//buffer off header
		/////////////////////////////////////////////////////////////////////////
		//get object name
		int position = 0;
		int length = message[position];
		position++;
		if(packet.getLength() <= (position+length))
			return null;
		String className = new String(message, position, length);
		position+=length;
		
		//get method name
		length = message[position];
		position++;
		if(packet.getLength() <= (position+length))
			return null;
		String methodName = new String(message, position, length);
		position+=length;
		
		//get request counter
		length = message[position];
		position++;
		if(packet.getLength() <= (position+length))
			return null;
		try{
			int messageNo = Integer.parseInt(new String(message, position, length));
			position+=length;
			/////////////////////////////////////////////////////////////////////////
			
			InetAddress sourceAddress = packet.getAddress();
			int sourcePort = packet.getPort();
			
			byte[] data = marshaller.subBytes(message, position, packet.getLength());
			
			//look up function
			if(functionMap.containsKey(methodName)){
				//if function exist, resolve it
				try{
					data = functionMap.get(methodName).resolve(messageNo, sourceAddress, sourcePort, data);
				}catch(Exception e){
					e.printStackTrace();
				}
				messageNo++;
				String counter = Integer.toString(messageNo);
				counter = (char)(counter.length()) + counter;
				data = marshaller.appendBytes(counter.getBytes(), data);
				//return reply
				return data;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return null;
	}
}
