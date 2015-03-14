package Server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Entity.SkeletonInterface;
import Marshaller.DataMarshaller;

//implementation of skeleton class for flight application
public class FlightSkeleton implements SkeletonInterface{
	//object(class) name
	private static String name = "Flight";
	//reference to flight implementation object
	private FlightImplementation flights;
	//data marshaller
	private DataMarshaller marshaller;
	//reply cache, maps a client ip to the last sent reply
	private Map<byte[], RequestHistory> bookFlightHistory;
	private Map<byte[], RequestHistory> monitorFlightHistory;
	
	public FlightSkeleton(FlightImplementation flights){
		this.flights = flights;
		this.marshaller = new DataMarshaller();
		bookFlightHistory = new HashMap();
		monitorFlightHistory = new HashMap();
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
		if(methodName.equals("getID")){
			//unmarshall parameters from message
			List objects = (List) marshaller.fromMessage(data);
			//pass parameter to method implementation
			//return marshalled reply
			return marshaller.toMessage(flights.getID((String)objects.get(0), (String)objects.get(1)));
		}else if(methodName.equals("getFlightDetails")){
			//unmarshall parameters from message
			int iD = (Integer)marshaller.fromMessage(data);
			//pass parameter to method implementation
			//return marshalled reply
			return marshaller.toMessage(flights.getFlightDetails(iD));
		}else if(methodName.equals("bookFlight")){
			//check if request is a repeat
			if(bookFlightHistory.containsKey(sourceAddress) && messageNo == bookFlightHistory.get(sourceAddress).getRequestNo()){
				//return cached reply
				return bookFlightHistory.get(sourceAddress).getReplyMessage();
			//else
			}else{
				//unmarshall parameters from message
				List objects = (List) marshaller.fromMessage(data);
				//pass parameter to method implementation
				//marshall reply
				byte[] reply = marshaller.toMessage(flights.bookFlight((Integer)objects.get(0), (Integer)objects.get(1)));
				//cache reply
				bookFlightHistory.put(sourceAddress, new RequestHistory(messageNo, reply));
				//return marshalled reply
				return reply;
			}
		}else if(methodName.equals("monitorFlight")){
			//check if request is a repeat
			if(monitorFlightHistory.containsKey(sourceAddress) && messageNo == monitorFlightHistory.get(sourceAddress).getRequestNo()){
				//return cached reply
				return monitorFlightHistory.get(sourceAddress).getReplyMessage();
			}else{
				
				//unmarshall parameters from message
				List objects = (List) marshaller.fromMessage(data);
				//pass parameter to method implementation
				boolean result =  flights.monitorFlight((Integer)objects.get(0), (Long)objects.get(1));
				try {
					//if monitor request accepted
					if(result)
						//add monitor stub to flight implementation
						flights.addMonitor(new FlightMonitorStub(new DatagramSocket(), packet.getAddress(), packet.getPort()));
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//marshall reply
				byte[] reply = marshaller.toMessage(result);
				//cache reply
				monitorFlightHistory.put(sourceAddress, new RequestHistory(messageNo, data));
				//return marshalled reply
				return reply;
			}
		}
		return null;
	}
}
