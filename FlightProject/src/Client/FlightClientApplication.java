package Client;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

import Entity.FlightDetails;
import Entity.FlightInterface;
import Marshaller.SimSocket;

//client application for flight project
public class FlightClientApplication {
	public static void login(FlightInterface flights){
		Scanner scan = new Scanner(System.in);
		while(true){
			System.out.println("Please enter username.");
			String user = scan.nextLine();
			System.out.println("Please enter password.");
			String password = scan.nextLine();
			if(flights.login(user, password)){
				System.out.println("Login Successful");
				break;
			}else{
				System.out.println("Login Failed. Please try again.");
			}
		}
	}
	
	public static void main(String[] args){
		//if no parameter provided
		if(args.length < 1){
			//initialize parameter to default
			args = new String[4];
			args[0] = "127.0.0.1";
			//args[0] = "155.69.144.89";
			args[1] = "5000";
			args[2] = "0.0";
			args[3] = "0";
		}
		//convert string to ip address
		String[] addressBytes = args[0].split("\\.");
		byte[] addr = new byte[addressBytes.length];
		for(int i=0; i<addressBytes.length; i++){
			addr[i] = (byte)Integer.parseInt(addressBytes[i]);
		}
		//convert string to port number
		int port = Integer.parseInt(args[1]);
		//convert string to packet loss rate
		double lossRate = Double.parseDouble(args[2]);
		//convert string to packet loss rate
		int networkDelay = Integer.parseInt(args[3]);
		FlightInterface flights;
		try {
			//create stimuated socket
			DatagramSocket socket = new SimSocket(lossRate, networkDelay);
			//create flight stub
			flights = new FlightStub(socket, InetAddress.getByAddress(addr), port);
			//request user to login
			login(flights);
			//enter problem loop
			while(true){
				//print menu
				System.out.println("Select service:");
				System.out.println("1. Finds flights from location1 to location2.");
				System.out.println("2. Get flight details.");
				System.out.println("3. Flight booking.");
				System.out.println("4. Monitor flight.");
				System.out.println("5. Check tickets booked.");
				System.out.println("6. Cancel tickets");
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
						System.out.println("Departure Time: " + new SimpleDateFormat("dd/MM/yy HH:mm zzz").format(new Date(flight.getTime())));
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
					if(numOfSeats <= 0){
						System.out.println("Invalid seat entry.");
					}else{
						switch(flights.bookFlight(fID, numOfSeats)){
						case -2:
							System.out.println("Please relog in.");
							login(flights);
							break;
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
				case 5:
				{
					System.out.println("Enter flight ID.");
					int fID = scan.nextInt();
					scan.nextLine();
					System.out.println(flights.viewTickets(fID) + " tickets booked for flight ID " + fID + ".");
				}
					break;
				case 6:
				{
					System.out.println("Enter flight ID.");
					int fID = scan.nextInt();
					scan.nextLine();
					System.out.println("Enter number of tickets.");
					int tickets = scan.nextInt();
					scan.nextLine();
					if(flights.cancelTickets(fID, tickets))
						System.out.println("Cancellation sucessful.");
					else
						System.out.println("Cancellation failed.");
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
