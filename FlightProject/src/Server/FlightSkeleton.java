package Server;
import java.net.DatagramPacket;
import java.util.List;
import java.util.Map;

import Entity.RemoteObjectInterface;
import Marshaller.DataMarshaller;


public class FlightSkeleton implements RemoteObjectInterface{
	private static String name = "Flight";
	private FlightImplementation flights;
	private DataMarshaller marshaller;
	private Map<byte[], RequestHistory> bookFlightHistory;
	private Map<byte[], RequestHistory> monitorFlightHistory;
	
	public FlightSkeleton(FlightImplementation flights){
		this.flights = flights;
		this.marshaller = new DataMarshaller();
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
		if(methodName.equals("getID")){
			List objects = (List) marshaller.fromMessage(data);
			return marshaller.toMessage(flights.getID((String)objects.get(0), (String)objects.get(1)));
		}else if(methodName.equals("getFlightDetails")){
			int iD = (Integer)marshaller.fromMessage(data);
			return marshaller.toMessage(flights.getFlightDetails(iD));
		}else if(methodName.equals("bookFlight")){
			if(bookFlightHistory.containsKey(sourceAddress) && messageNo == bookFlightHistory.get(sourceAddress).getRequestNo()){
				return bookFlightHistory.get(sourceAddress).getReplyMessage();
			}else{
				List objects = (List) marshaller.fromMessage(data);
				byte[] reply = marshaller.toMessage(flights.bookFlight((Integer)objects.get(0), (Integer)objects.get(1)));
				bookFlightHistory.put(sourceAddress, new RequestHistory(messageNo, reply));
				return reply;
			}
		}else if(methodName.equals("monitorFlight")){
			if(monitorFlightHistory.containsKey(sourceAddress) && messageNo == monitorFlightHistory.get(sourceAddress).getRequestNo()){
				return monitorFlightHistory.get(sourceAddress).getReplyMessage();
			}else{
				List objects = (List) marshaller.fromMessage(data);
				byte[] reply = marshaller.toMessage(flights.monitorFlight((Integer)objects.get(0), (Long)objects.get(1)));
				monitorFlightHistory.put(sourceAddress, new RequestHistory(messageNo, data));
				return reply;
			}
		}
		return null;
	}
}
