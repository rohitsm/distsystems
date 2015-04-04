package Client;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

import Entity.FlightDetails;
import Entity.FlightInterface;
import Marshaller.SimSocket;

//client application for flight project
public class FlightClientApplication {
	
	//read integer
	public static int readInt(Scanner scan, String message){
		int value;
		while(true){
			//print request message
			System.out.print(message);
			try{
				//if input is integer
				value = Integer.parseInt(scan.nextLine());
				//return input
				return value;
			}catch(NumberFormatException e){
				//else display error message and request input again
				System.out.println("Invalid Input. Please re-enter value. ");
			}
		}
	}
	
	//read long
	public static long readLong(Scanner scan, String message){
		long value;
		while(true){
			//print request message
			System.out.print(message);
			try{
				//if input is long
				value = Long.parseLong(scan.nextLine());
				//return input
				return value;
			}catch(NumberFormatException e){
				//else display error message and request input again
				System.out.println("Invalid Input. Please re-enter value. ");
			}
		}
	}
	
	//login request
	public static void login(FlightInterface flights){
		Scanner scan = new Scanner(System.in);
		while(true){
			//prompt user for username
			System.out.println("Please enter username.");
			String user = scan.nextLine();
			//prompt user for password
			System.out.println("Please enter password.");
			String password = scan.nextLine();
			//send request to remote object
			if(flights.login(user, password)){
				//if login succeed, print success message
				System.out.println("Login Successful");
				break;
			}else{
				//else print fail message and prompt for login fields again
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
			args[2] = "0.5";
			args[3] = "2000";
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
		//convert string to network delay
		int networkDelay = Integer.parseInt(args[3]);
		FlightInterface flights;
		try {
			//create stimulated socket
			DatagramSocket socket = new SimSocket(lossRate, networkDelay);
			//create flight stub
			flights = new FlightStub(socket, InetAddress.getByAddress(addr), port);
			//request user to login
			login(flights);
			//enter program loop
			while(true){
				//print menu
				System.out.println("Select service:");
				System.out.println("1. Finds flights from location1 to location2.");
				System.out.println("2. Get flight details.");
				System.out.println("3. Flight booking.");
				System.out.println("4. Monitor flight.");
				System.out.println("5. Check tickets booked.");
				System.out.println("6. Cancel tickets");
				System.out.println("7. Exit");
				Scanner scan = new Scanner(System.in);
				//prompt user for choice
				int choice = readInt(scan, "");
				switch(choice){
				case 1:
				{
					//prompt user for source location
					System.out.println("Please source location.");
					String source = scan.nextLine();
					//prompt user for destination location
					System.out.println("Please destination location.");
					String destination = scan.nextLine();
					//send request to remote object
					List<Integer> fIDs = flights.getID(source, destination);
					//if empty list is returned
					if(fIDs.isEmpty()){
						//display no flights found
						System.out.println("No matching flights found.");
					}else{
						//else, display list of flight IDs
						for(int fID:fIDs){
							System.out.println(fID);
						}
					}
				}
					break;
				case 2:
				{
					//prompt user for flight ID
					int fID = readInt(scan, "Enter flight ID.\n");
					//send request to remote object
					FlightDetails flight = flights.getFlightDetails(fID);
					//if no flight details
					if(flight==null){
						//display flight ID not found
						System.out.println("Flight ID not found.");
					}else{
						//else display flight details
						System.out.println("Departure Time: " + new SimpleDateFormat("dd/MM/yy HH:mm zzz").format(new Date(flight.getTime())));
						System.out.println("Airfare: " + String.format("$%.2f", flight.getAirfare()));
						System.out.println("Seats Available: " + flight.getAvailableSeats());
					}
				}
					break;
				case 3:
				{
					//prompt user for flight ID
					int fID = readInt(scan, "Enter flight ID.\n");
					//prompt user for number of seats
					int numOfSeats = readInt(scan, "Enter number of seats.\n");
					//if number of seats not positive
					if(numOfSeats <= 0){
						//display invalid seat message
						System.out.println("Invalid seat entry.");
					}else{
						//else, send request to remote object
						switch(flights.bookFlight(fID, numOfSeats)){
						case -2:
							//if user not found, inform user to relog in
							System.out.println("Please relog in.");
							//request login
							login(flights);
							break;
						case -1:
							//if flight ID not found, inform user so
							System.out.println("Flight ID not found.");
							break;
						case 0:
							//if insufficient seats, inform user so
							System.out.println("Insufficient seats.");
							break;
						case 1:
							//if booking successful, inform user so
							System.out.println("Booking successful.");
							break;
						}
					}
				}
					break;
				case 4:
				{
					//prompt user for flight ID
					int fID = readInt(scan, "Enter flight ID.\n");
					long duration = readLong(scan, "Enter duration in ms.\n");
					//send request to remote object
					//if request fail
					if(!flights.monitorFlight(fID, duration)){
						//inform user flight ID not found
						System.out.println("Flight ID not found.");
					}
					
				}
					break;
				case 5:
				{
					//prompt user for flight ID
					int fID = readInt(scan, "Enter flight ID.\n");
					//send request to remote object
					//display number of tickets
					System.out.println(flights.viewTickets(fID) + " tickets booked for flight ID " + fID + ".");
				}
					break;
				case 6:
				{
					//prompt user for flight ID
					int fID = readInt(scan, "Enter flight ID.\n");
					int tickets = readInt(scan, "Enter number of tickets.\n");
					//send request to remote object
					//display cancellation success result
					if(flights.cancelTickets(fID, tickets))
						System.out.println("Cancellation sucessful.");
					else
						System.out.println("Cancellation failed.");
				}
					break;
				case 7:
					//terminate system
					System.out.println("System terminating...");
					System.exit(0);
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
