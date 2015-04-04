package Server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Entity.Skeleton;
import Entity.SkeletonFunctionInterface;
import Marshaller.DataMarshaller;

//implementation of skeleton class for flight application
public class FlightSkeleton extends Skeleton{
	//object(class) name
	private static String name = "Flight";
	//reference to flight implementation object
	private FlightImplementation flights;
	//reply cache, maps a client ip to the last sent reply
	private Map<InetAddress, RequestHistory> bookFlightHistory;
	private Map<InetAddress, RequestHistory> monitorFlightHistory;
	private Map<InetAddress, RequestHistory> cancelTicketHistory;
	
	public FlightSkeleton(FlightImplementation flights){
		this.flights = flights;
		marshaller = new DataMarshaller();
		bookFlightHistory = new HashMap();
		monitorFlightHistory = new HashMap();
		cancelTicketHistory = new HashMap();
		
		//initialise function map
		functionMap = new HashMap();
		
		functionMap.put("getID", new SkeletonFunctionInterface(){
			@Override
			public byte[] resolve(int messageNo, InetAddress sourceAddress, int sourcePort, byte[] data) {
				//unmarshal parameters from message
				List objects = (List) marshaller.fromMessage(data);
				//pass parameter to method implementation
				//return marshalled reply
				return marshaller.toMessage(flights.getID((String)objects.get(0), (String)objects.get(1)));
			}
		});
		
		functionMap.put("getFlightDetails", new SkeletonFunctionInterface(){
			@Override
			public byte[] resolve(int messageNo, InetAddress sourceAddress, int sourcePort, byte[] data) {
				//unmarshal parameters from message
				int iD = (Integer)marshaller.fromMessage(data);
				//pass parameter to method implementation
				//return marshalled reply
				return marshaller.toMessage(flights.getFlightDetails(iD));
			}
			
		});
		
		functionMap.put("bookFlight", new SkeletonFunctionInterface(){
			@Override
			public byte[] resolve(int messageNo, InetAddress sourceAddress, int sourcePort, byte[] data) {
				//check if request is a repeat
				if(bookFlightHistory.containsKey(sourceAddress) && messageNo == bookFlightHistory.get(sourceAddress).getRequestNo()){
					//return cached reply
					return bookFlightHistory.get(sourceAddress).getReplyMessage();
				//else
				}else{
					//set user of flight implementation
					setUser(sourceAddress);
					//unmarshal parameters from message
					List objects = (List) marshaller.fromMessage(data);
					//pass parameter to method implementation
					//marshal reply
					byte[] reply = marshaller.toMessage(flights.bookFlight((Integer)objects.get(0), (Integer)objects.get(1)));
					//cache reply
					bookFlightHistory.put(sourceAddress, new RequestHistory(messageNo, reply));
					//return marshalled reply
					return reply;
				}
			}
			
		});
	
		functionMap.put("monitorFlight", new SkeletonFunctionInterface(){
			@Override
			public byte[] resolve(int messageNo, InetAddress sourceAddress, int sourcePort, byte[] data) {
				//check if request is a repeat
				if(monitorFlightHistory.containsKey(sourceAddress) && messageNo == monitorFlightHistory.get(sourceAddress).getRequestNo()){
					//return cached reply
					return monitorFlightHistory.get(sourceAddress).getReplyMessage();
				}else{
					
					//unmarshal parameters from message
					List objects = (List) marshaller.fromMessage(data);
					//pass parameter to method implementation
					boolean result =  flights.monitorFlight((Integer)objects.get(0), (Long)objects.get(1));
					try {
						//if monitor request accepted
						if(result)
							//add monitor stub to flight implementation
							flights.addMonitor(new FlightMonitorStub(new DatagramSocket(), sourceAddress, sourcePort));
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//marshal reply
					byte[] reply = marshaller.toMessage(result);
					//cache reply
					monitorFlightHistory.put(sourceAddress, new RequestHistory(messageNo, reply));
					//return marshalled reply
					return reply;
				}
			}
		});
		
		functionMap.put("login", new SkeletonFunctionInterface(){
			@Override
			public byte[] resolve(int messageNo, InetAddress sourceAddress, int sourcePort, byte[] data) {
				//unmarshal parameters from message
				List objects = (List) marshaller.fromMessage(data);
				//pass parameter to method implementation
				String user = (String)objects.get(0);
				boolean result = flights.login(user, (String)objects.get(1));
				//if login successful
				if(result){
					//register user
					flights.registerUser(sourceAddress, user);
					//clear cache for previous user using the same ip
					clearCache(sourceAddress);
				}
				//marshal reply
				return marshaller.toMessage(result);
			}
		});
		
		functionMap.put("viewTickets", new SkeletonFunctionInterface(){
			@Override
			public byte[] resolve(int messageNo, InetAddress sourceAddress, int sourcePort, byte[] data) {
				//set user of flight implementation
				setUser(sourceAddress);
				//unmarshal parameters from message
				int iD = (Integer)marshaller.fromMessage(data);
				//pass parameter to method implementation
				//marshal reply
				return marshaller.toMessage(flights.viewTickets(iD));
			}
		});
		
		functionMap.put("cancelTickets", new SkeletonFunctionInterface(){
			@Override
			public byte[] resolve(int messageNo, InetAddress sourceAddress, int sourcePort, byte[] data) {
				//check if request is a repeat
				if(cancelTicketHistory.containsKey(sourceAddress) && messageNo == cancelTicketHistory.get(sourceAddress).getRequestNo()){
					//return cached reply
					return cancelTicketHistory.get(sourceAddress).getReplyMessage();
				}else{
					//set user of flight implementation
					setUser(sourceAddress);
					//unmarshal parameters from message
					List objects = (List) marshaller.fromMessage(data);
					//pass parameter to method implementation
					//marshal reply
					byte[] reply = marshaller.toMessage(flights.cancelTickets((Integer)objects.get(0), (Integer)objects.get(1)));
					//cache reply
					cancelTicketHistory.put(sourceAddress, new RequestHistory(messageNo, reply));
					//return marshalled reply
					return reply;
				}
			}
		});
	}
	
	//set flight implementation user from id address
	private void setUser(InetAddress address){
		flights.setUser(address);
	}
	
	//removes cache record of a specify id address
	private void clearCache(InetAddress address){
		bookFlightHistory.remove(address);
		monitorFlightHistory.remove(address);
		cancelTicketHistory.remove(address);
	}
	
	public String getName(){
		return name;
	}
}
