'''
Created on 2 Apr, 2015

@author: Ronald
'''
from marshaller.DataMarshaller import DataMarshaller
import time
import socket

class FlightMonitorSkeleton(object):
    '''
    classdocs
    '''
    def __init__(self, socket, flight_monitor):
        '''
        Constructor
        '''
        #object(class) name
        self.name = 'FlightMonitor'
        #socket to send/receive messages
        self.socket = socket
        #reference to flight monitor implementation object
        self.flight_monitor = flight_monitor
        #data marshaller
        self.marshaller = DataMarshaller()
        #method map - maps method name to method handler
        self.method_map = {"update":  self.resolve_update}
    
    def resolve_update(self, message_no, source_address, source_port, data):
        #unmarshal parameters from message
        available_seats = self.marshaller.from_bytes(data)
        #if invalid data return ignore
        if available_seats is None:
            return
        #pass parameter to method implementation
        self.flight_monitor.update(available_seats)
        #return none message
        return self.marshaller.to_bytes(None)
    
    #listen until monitor period has ended
    def listen_until(self, duration):
        #record end time
        end_time = time.time() + duration
        #set time out to duration
        self.socket.settimeout(duration)
        #keep listening for messages
        while True:
            try:
                #wait for incoming message
                self.listen()
                #monitor period is not over
                if time.time() < end_time:
                    #set time out to remaining monitor time
                    self.socket.settimeout(end_time - time.time())
                else:
                    return
            except socket.timeout as e:
                #if time out, stop listening
                break
    
    #wait for incoming message and forward them to the remote object
    def listen(self):
        #wait for incoming packet
        msg = self.socket.recvfrom(65536)
        #forward message to remote object
        result = self.process_message(msg)
        #if there is a reply message
        if not result is None:
            #send reply message back to client
            self.socket.sendto(result, msg[1])

    #process a packet and returns reply message in bytes
    def process_message(self, msg):
        data = msg[0]
        #process header
        try:
            #retrieve object name
            position = 0
            length = data[position]
            position += 1
            next_position = position + length
            class_name = str(data[position:next_position], 'utf-8')
            
            #retrieve method name
            position = next_position
            length = data[position]
            position += 1
            next_position = position + length
            method_name = str(data[position:next_position], 'utf-8')
            
            #retrieve message no
            position = next_position
            length = data[position]
            position += 1
            next_position = position + length
            message_no = int(str(data[position:next_position], 'utf-8'))
            
            position = next_position
            source_address = msg[1][0]
            source_port = msg[1][1]
            
            #retrieve data bytes
            data = data[position:]
            
            #look up method handler
            data = self.method_map[method_name](message_no, source_address, source_port, data)
            #add message no to reply
            message_no += 1
            counter = str(message_no)
            data = bytes(chr(len(counter)) + counter, 'utf-8') + data
            #return reply
            return data
        except IndexError | ValueError as e:
            return


class FlightMonitorImplementation(object):
    '''
    classdocs
    '''
    def __init__(self, id):
        '''
        Constructor
        '''
        #flight ID
        self.id = id
        #last notified available seats
        self.available_seats = None
        
    def update(self, available_seats):
        #if number of available seat changes
        if self.available_seats != available_seats:
            #print new current available seats
            print("Flight " + str(self.id) + " has " + str(available_seats) + " seats remaining.")
            #update available seats
            self.available_seats = available_seats
