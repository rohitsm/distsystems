'''
Created on 2 Apr, 2015

@author: Ronald
'''
import random
import time
import socket
from client.FlightDetails import FlightDetails
from marshaller.DataMarshaller import DataMarshaller
from client.FlightMonitor import FlightMonitorImplementation, FlightMonitorSkeleton

class FlightStub(object):
    '''
    classdocs
    '''
    def __init__(self, socket, host, port):
        '''
        Constructor
        '''
        #object(class) name
        self.name = "Flight"
        self.socket = socket
        self.host = host
        self.port = port
        self.marshaller = DataMarshaller()
        self.packet_counter = random.randint(0, 2147483647)
    
    #send a message and wait for 1 second for reply
    def send(self, message):
        #set time out to 1 sec
        self.socket.settimeout(1)
        #send message
        self.socket.sendto(message, (self.host, self.port))
        #wait for reply message
        reply = self.socket.recvfrom(65536)
        #return reply data
        return reply[0]
    
    #send a message for a duration
    def send_until(self, message, duration):
        start_time = time.time()
        while True:
            #if timeout threshold reached
            if((time.time()-start_time) > duration):
                #raise timeout exception
                raise socket.timeout
            try:
                #send message
                return self.send(message)
            except socket.timeout as e:
                #catch timeout exception
                pass
                
    #create message header
    #header consist of object name, method name, packet counter
    def create_packet_header(self, method_name):
        #format(where  is the length of the item):
        #-length of object name(1 byte)
        #-object name(X byte)
        #-length of method name(1 byte)
        #-method name(X byte)
        #-length of packet counter string (1 byte)
        #-packet counter(X byte)
        counter = str(self.packet_counter)
        header = ""
        for item in [self.name, method_name, counter]:
            header += chr(len(item)) + item
        #increase packet counter after generating header
        self.packet_counter+=1
        return bytes(header, 'utf-8')
    
    def send_request(self, method_name, parameters, expected_class):
        #combine header with parameters
        msg = self.create_packet_header(method_name) + parameters
        #send message for 5 mins
        start_time = time.time()
        duration = 5*60
        while True:
            remaining_time = duration - (time.time() - start_time)
            if remaining_time <= 0:
                break
            reply = self.send_until(msg, remaining_time)
            try:
                length = int(reply[0])
                message_no = int(reply[1:length+1])
                #convert data
                data = self.marshaller.from_bytes(reply[length+1:])
                #message no is correct and data type is expected, return data
                if message_no == self.packet_counter:
                    if data is None:
                        return
                    if not expected_class is None:
                        if data.__class__==expected_class:
                            return data
            except IndexError | ValueError as e:
                print(e)
        
    
    #returns a list of flight IDs matching a given flight source and destination
    def get_id(self, source, destination):
        #prepare parameters
        parameters = self.marshaller.to_bytes(source) + self.marshaller.to_bytes(destination)
        #send request
        data = self.send_request("getID", parameters, list)
        if data is None:
            data = list()
        #return result
        return data
    
    #returns flight details given a flight ID
    def get_flight_details(self, id):
        #prepare parameters
        parameters = self.marshaller.to_bytes(id, 4)
        #send request
        data = self.send_request("getFlightDetails", parameters, FlightDetails)
        #return result
        return data
    
    #returns result status of seat booking given the flight ID and number of seats to book
    def book_flight(self, id, seats):
        #prepare parameters
        parameters = self.marshaller.to_bytes(id, 4) + self.marshaller.to_bytes(seats, 4)
        #send request
        data = self.send_request("bookFlight", parameters, int)
        if data is None:
            data = -2
        #return result
        return data
    
    #returns monitor successful status given flight ID and duration to monitor
    def monitor_flight(self, id, msec):
        #prepare parameters
        parameters = self.marshaller.to_bytes(id, 4) + self.marshaller.to_bytes(msec, 8)
        #send request
        data = self.send_request("monitorFlight", parameters, bool)
        if data is None:
            data = False
        #if successful
        if data:
            #create monitor implementation and skeleton
            monitor = FlightMonitorImplementation(id)
            server = FlightMonitorSkeleton(self.socket, monitor)
            #listen for updates
            server.listen_until(msec/1000.0)
        #return result
        return data

    #additional functions
    #login to flight system
    def login(self, user, password):
        #prepare parameters
        parameters = self.marshaller.to_bytes(user) + self.marshaller.to_bytes(password)
        #send request
        data = self.send_request("login", parameters, bool)
        if data is None:
            data = False
        #return result
        return data
    
    #returns number of tickets booked by user give the flight ID
    def view_tickets(self, id):
        #prepare parameters
        parameters = self.marshaller.to_bytes(id, 4)
        #send request
        data = self.send_request("viewTickets", parameters, int)
        if data is None:
            data = -1
        return data
    
    #cancels booked tickets of user given the flight ID and the number of tickets
    def cancel_tickets(self, id, tickets):
        #prepare parameters
        parameters = self.marshaller.to_bytes(id, 4) + self.marshaller.to_bytes(tickets, 4)
        #send request
        data = self.send_request("cancelTickets", parameters, bool)
        if data is None:
            data = False
        #return result
        return data
    