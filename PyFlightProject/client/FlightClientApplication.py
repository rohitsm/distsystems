'''
Created on 2 Apr, 2015

@author: Ronald
'''
from client.FlightStub import FlightStub
import socket
import time
import datetime

#client application for flight project
def login(flights):
    while True:
        user = input("Please enter username.\n")
        password = input("Please enter password.\n")
        if flights.login(user, password):
            print("Login Sucessful.")
            return;
        else:
            print("Login Failed. Please try again.")
        
def main(args):
    #if no parameters provided
    if len(args) < 1:
        #initialize parameter to default
        args = list()
        args.append("155.69.144.89")
        #args.append("127.0.0.1")
        args.append("5000")
    #get ip address from arguments
    ipaddress = args[0]
    #get port from arguments
    try:
        port = int(args[1])
        #create socket
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        #create flight stub
        flights = FlightStub(s, ipaddress, port)
        #request user login
        login(flights)
        #enter program loop
        while True:
        #print menu
            print("Select service:")
            print("1. Find flights from Location1 to Location2.")
            print("2. Get flight details.")
            print("3. Flight booking.")
            print("4. Monitor flight.")
            print("5. Check tickets booked.")
            print("6. Cancel tickets")
            choice = int(input())
            {
            1:  find_flight,
            2:  get_flight_details,
            3:  book_flight ,
            4:  monitor_flight ,
            5:  check_tickets ,
            6:  cancel_tickets
            }[choice](flights)
        
    except Exception as e:
        print(e)

def find_flight(flights):
    source = input("Please enter source location.\n")
    destination = input("Please enter desintation location.\n")
    f_ids = flights.get_id(source, destination)
    if len(f_ids)==0:
        print("No matching flights found.")
    else:
        for f_id in f_ids:
            print(f_id)

def get_flight_details(flights):
    f_id = int(input("Enter flight ID.\n"))
    flight = flights.get_flight_details(f_id)
    if flight is None:
        print("Flight ID not found.")
    else:
        print("Departure Time: " + datetime.datetime.fromtimestamp(flight.get_time()/1000.0).strftime("%d/%m/%y %H:%M") + " " + time.tzname[0])
        print("Airfare: " + str.format("${:.2f}", flight.get_airfare()))
        print("Seats Available: " + str(flight.get_available_seats()))
    
def book_flight(flights):
    f_id = int(input("Enter flight ID.\n"))
    seats = int((input("Enter number of seats.\n")))
    if seats < 1:
        print("Invalid seat entry")
    else:
        status = flights.book_flight(f_id, seats)
        output = {
        -2: "Please relog in.",
        -1: "Flight ID not found.",
        0: "Insufficient seats.",
        1: "Booking successful. "
        }[status]
        print(output)
        if(status == -2):
            login(flights)
        
def monitor_flight(flights):
    f_id = int(input("Enter flight ID.\n"))
    duration = int(input("Enter duration in ms.\n"))
    if not flights.monitor_flight(f_id, duration):
        print("Flight ID not found.")

def check_tickets(flights):
    f_id = int(input("Enter flight ID.\n"))
    print(str(flights.view_tickets(f_id)) + " ticket(s) booked for flight ID " + str(f_id) + ".")

def cancel_tickets(flights):
    f_id = int(input("Enter flight ID.\n"))
    tickets = int(input("Enter number of tickets.\n"))
    if flights.cancel_tickets(f_id, tickets):
        print("Cancellation successful.")
    else:
        print("Cancellation failed.")      
    

if __name__ == '__main__':
    main(list())