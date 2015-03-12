package Client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import Entity.FlightDetails;
import Entity.FlightInterface;
import Entity.Stub;
import Marshaller.DataMarshaller;
import Marshaller.SimSocket;


public class FlightStub extends Stub implements FlightInterface{
	private String name = "Flight";
	
	public FlightStub(DatagramSocket socket, InetAddress host, int port){
		super(socket, host, port);
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public List<Integer> getID(String source, String destination) {
		// TODO Auto-generated method stub
		byte[] message = createPacketHeader("getID");
		message = marshaller.appendBytes(message, marshaller.toMessage(source));
		message = marshaller.appendBytes(message, marshaller.toMessage(destination));
		try{
			message = sendUntil(message, 5*60);
			return (List<Integer>)marshaller.fromMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public FlightDetails getFlightDetails(int iD) {
		// TODO Auto-generated method stub
		byte[] message = createPacketHeader("getFlightDetails");
		message = marshaller.appendBytes(message, marshaller.toMessage(iD));
		try{
			message = sendUntil(message, 5*60);
			return (FlightDetails)marshaller.fromMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int bookFlight(int iD, int seats) {
		// TODO Auto-generated method stub
		byte[] message =  createPacketHeader("bookFlight");
		message = marshaller.appendBytes(message, marshaller.toMessage(iD));
		message = marshaller.appendBytes(message, marshaller.toMessage(seats));
		try{
			message = sendUntil(message, 5*60);
			return (int)marshaller.fromMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -2;
	}

	@Override
	public boolean monitorFlight(int iD, long msec) {
		// TODO Auto-generated method stub
		byte[] message = createPacketHeader("monitorFlight");
		message = marshaller.appendBytes(message, marshaller.toMessage(iD));
		message = marshaller.appendBytes(message, marshaller.toMessage(msec));
		try{
			message = sendUntil(message, 5*60);
			boolean result = (boolean)marshaller.fromMessage(message);
			if(result){
				FlightMonitorImplementation monitor = new FlightMonitorImplementation(iD);
				FlightMonitorSkeleton server = new FlightMonitorSkeleton(socket, monitor);
				server.listen(msec);
			}
			return (boolean)marshaller.fromMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args){
		if(args.length < 1){
			args = new String[3];
			args[0] = "127.0.0.1";
			args[1] = "5000";
			args[2] = "0.0";
		}
		String[] addressBytes = args[0].split("\\.");
		byte[] addr = new byte[addressBytes.length];
		for(int i=0; i<addressBytes.length; i++){
			addr[i] = (byte)Integer.parseInt(addressBytes[i]);
		}
		int port = Integer.parseInt(args[1]);
		double lossRate = Double.parseDouble(args[2]);
		FlightInterface flights;
		try {
			DatagramSocket socket = new SimSocket(lossRate);
			flights = new FlightStub(socket, InetAddress.getByAddress(addr), port);
			while(true){
				System.out.println("Select service:");
				System.out.println("1. Finds flights from location1 to location2.");
				System.out.println("2. Get flight details.");
				System.out.println("3. Flight booking.");
				System.out.println("4. Monitor flight.");
				Scanner scan = new Scanner(System.in);
				int choice = scan.nextInt();
				scan.nextLine();
				switch(choice){
				case 1:
				{
					System.out.println("Please source location.");
					String source = scan.nextLine();
					System.out.println("Please destination location.");
					String destination = scan.nextLine();
					List<Integer> fIDs = flights.getID(source, destination);
					if(fIDs.isEmpty()){
						System.out.println("No matching flights found.");
					}else{
						for(int fID:fIDs){
							System.out.println(fID);
						}
					}
				}
					break;
				case 2:
				{
					System.out.println("Enter flight ID.");
					int fID = scan.nextInt();
					scan.nextLine();
					FlightDetails flight = flights.getFlightDetails(fID);
					if(flight==null){
						System.out.println("Flight ID not found.");
					}else{
						System.out.println("Departure Time: " + new SimpleDateFormat("dd/MM/yy HH:mm").format(new Date(flight.getTime())));
						System.out.println("Airfare: " + String.format("$%.2f", flight.getAirfare()));
						System.out.println("Seats Available: " + flight.getAvailableSeats());
					}
				}
					break;
				case 3:
				{
					System.out.println("Enter flight ID.");
					int fID = scan.nextInt();
					scan.nextLine();
					System.out.println("Enter number of seats.");
					int numOfSeats = scan.nextInt();
					scan.nextLine();
					switch(flights.bookFlight(fID, numOfSeats)){
					case -1:
						System.out.println("Flight ID not found.");
						break;
					case 0:
						System.out.println("Insufficient seats.");
						break;
					case 1:
						System.out.println("Booking successful.");
						break;
					}
				}
					break;
				case 4:
				{
					System.out.println("Enter flight ID.");
					int fID = scan.nextInt();
					scan.nextLine();
					System.out.println("Enter duration in ms.");
					long duration = scan.nextLong();
					scan.nextLine();
					if(!flights.monitorFlight(fID, duration)){
						System.out.println("Flight ID not found.");
					}
					
				}
					break;
				}
			}
		} catch (UnknownHostException | SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//*/
	}
}
