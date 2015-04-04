'''
Created on 2 Apr, 2015

@author: Ronald
'''

#python version of the java Flight Details Class
class FlightDetails(object):
    '''
    classdocs
    '''
    def __init__(self, time=0, airfare=0.0, available_seats=0):
        '''
        Constructor
        '''
        self.time = time
        self.airfare = airfare
        self.available_seats = available_seats
    
    def get_time(self):
        return self.time
    
    def set_time(self, time):
        self.time = time
        
    def get_airfare(self):
        return self.airfare
    
    def set_airfare(self, airfare):
        self.airfare = airfare
        
    def get_available_seats(self):
        return self.available_seats
    
    def set_available_seats(self, available_seats):
        self.available_seats = available_seats  