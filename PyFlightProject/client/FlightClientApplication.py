'''
Created on 2 Apr, 2015

@author: Ronald
'''
from client.FlightStub import FlightStub
import socket
import time
import datetime
import sys

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
            print("7. Exit")
            #get choice from user
            choice = int(input())
            {
            1:  find_flight,
            2:  get_flight_details,
            3:  book_flight ,
            4:  monitor_flight ,
            5:  check_tickets ,
            6:  cancel_tickets,
            7:  terminate
            }[choice](flights)
        
    except Exception as e:
        print(e)

def find_flight(flights):
    #prompt user for source location
    source = input("Please enter source location.\n")
    #prompt user for destination location
    destination = input("Please enter desintation location.\n")
    #send request to remote object
    f_ids = flights.get_id(source, destination)
    #if empty list
    if len(f_ids)==0:
        #display no flights found
        print("No matching flights found.")
    else:
        #else, display list of flight IDs
        for f_id in f_ids:
            print(f_id)

def get_flight_details(flights):
    #prompt user for flight ID
    f_id = int(input("Enter flight ID.\n"))
    #send request to remote object
    flight = flights.get_flight_details(f_id)
    #if no flight details
    if flight is None:
        #display flight ID not found
        print("Flight ID not found.")
    else:
        #else, display flight details
        print("Departure Time: " + datetime.datetime.fromtimestamp(flight.get_time()/1000.0).strftime("%d/%m/%y %H:%M") + " " + time.tzname[0])
        print("Airfare: " + str.format("${:.2f}", flight.get_airfare()))
        print("Seats Available: " + str(flight.get_available_seats()))
    
def book_flight(flights):
    #prompt user for flight ID
    f_id = int(input("Enter flight ID.\n"))
    #prompt user for number of seats
    seats = int((input("Enter number of seats.\n")))
    #check number of seats is positive
    if seats < 1:
        print("Invalid seat entry")
    else:
        #send request to remote object
        status = flights.book_flight(f_id, seats)
        output = {
        #if no user logged in, ask user to login
        -2: "Please relog in.",
        #if flight ID not found, display so.
        -1: "Flight ID not found.",
        #if insufficient seats, display so.
        0: "Insufficient seats.",
        #if booking succeed, display so
        1: "Booking successful. "
        }[status]
        print(output)
        #if no user logged in
        if(status == -2):
            #request user login
            login(flights)
        
def monitor_flight(flights):
    #prompt user for flight ID
    f_id = int(input("Enter flight ID.\n"))
    #prompt user for duration
    duration = int(input("Enter duration in ms.\n"))
    #send request to remote object
    if not flights.monitor_flight(f_id, duration):
        #if fail, display flight ID not found
        print("Flight ID not found.")

def check_tickets(flights):
    #prompt user for flight ID
    f_id = int(input("Enter flight ID.\n"))
    #send request to remote object
    #display number of tickets booked
    print(str(flights.view_tickets(f_id)) + " ticket(s) booked for flight ID " + str(f_id) + ".")

def cancel_tickets(flights):
    #prompt user for flight ID
    f_id = int(input("Enter flight ID.\n"))
    #prompt user for number of tickets
    tickets = int(input("Enter number of tickets.\n"))
    #send request to remote object
    #print request result
    if flights.cancel_tickets(f_id, tickets):
        print("Cancellation successful.")
    else:
        print("Cancellation failed.")      
    

def terminate(flights):
    #terminate system
    print("System terminating.")
    sys.exit()

if __name__ == '__main__':
    main(list())