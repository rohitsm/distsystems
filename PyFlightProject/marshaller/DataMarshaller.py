'''
Created on 1 Apr, 2015

@author: Ronald
'''
import struct
from client.FlightDetails import FlightDetails

#byte representation for data types
NONEBYTE = 0;
BOOLBYTE = 1;
INT4BYTE = 2;
INT8BYTE = 3;
STRBYTE = 4;
ARRBYTE = 5;
FLOAT4BYTE = 6;
FLIGHTDETAILSBYTE = 7;
REMOTEOBJBYTE = 8;

class DataMarshaller(object):
    '''
    classdocs
    '''
    def __init__(self):
        '''
        Constructor
        '''
        bool_conv = BoolConvertor()
        int4_conv = IntConvertor(4)
        int8_conv = IntConvertor(8)
        str_conv = StrConvertor()
        arr_conv = ArrConvertor()
        float4_conv = FloatConvertor(4)
        flightdetails_conv = FlightDetailsConvertor()
        #maps type number to convertor object
        self.conv_map = {
                    NONEBYTE:   None,
                    BOOLBYTE:   bool_conv,
                    INT4BYTE:   int4_conv,
                    INT8BYTE:   int8_conv,
                    STRBYTE:    str_conv,
                    ARRBYTE:    arr_conv,
                    FLOAT4BYTE: float4_conv,
                    FLIGHTDETAILSBYTE:  flightdetails_conv
                    }
        #maps class object to type number
        self.class_map = {
                          bool:   BOOLBYTE,
                          int:    {4:INT4BYTE, 8:INT8BYTE},
                          str:    STRBYTE,
                          list:   ARRBYTE,
                          float:  FLOAT4BYTE,
                          FlightDetails:  FLIGHTDETAILSBYTE
                          }
        
    #retrieve data convertor based on object to be converted
    def get_data_convertor(self, data, size=0):
        try:
            #retrieve type number
            type_value = self.class_map[data.__class__]
            #if type number is a dictionary
            if isinstance(type_value, dict):
                #use size parameter to retrieve number
                type_value = type_value[size]
            #retrieve convertor using type number
            conv = self.conv_map[type_value]
            #initial type info
            type_byte = bytes([type_value])
            #if type is array or string
            if type_value == ARRBYTE or type_value == STRBYTE:
                #if type is array
                if type_value == ARRBYTE:
                    #get inner convertor
                    inner_type_byte, inner_conv = self.get_data_convertor(data[0], size)
                    conv.set_inner_convertor(inner_conv)
                    #append inner convertor type info
                    type_byte += inner_type_byte
                #set size for convertor
                conv.set_size(len(data))
                #append convertor size
                type_byte += bytes([len(data)])
            #return type info and convertor
            return type_byte, conv
        except KeyError as e:
            return bytes([NONEBYTE]), None
    
    #convert data object to bytes
    def to_bytes(self, data, size = 0):
        #retrieve type info and convertor
        type_byte, conv = self.get_data_convertor(data, size)
        if conv is None:
            return type_byte
        else:
            #return type info and converted data
            return type_byte + conv.to_bytes(data)
    
    #get convertor based on bytes
    def get_byte_convertor(self, b):
        #get type value from  first bit
        type_value = b[0]
        b = b[1:]
        #get convertor using type value
        conv = self.conv_map[type_value]
        #if is an array or a string
        if type_value == ARRBYTE or type_value == STRBYTE:
            #if is an array
            if type_value == ARRBYTE:
                #use next bits to retrieve inner convertor
                b, inner_conv = self.get_byte_convertor(b)
                conv.set_inner_convertor(inner_conv)
            #use next bit to retrieve convertor size
            conv.set_size(b[0])
            b = b[1:]
        #return unused bytes and convertor
        return b, conv
    
    #converts bytes to data objects
    def from_bytes(self, b):
        #initialise data buffer as empty list
        data_buffer = list()
        #while there is still unused bytes
        while len(b) > 0:
            data = None
            #retrieve convertor
            b, conv = self.get_byte_convertor(b)
            #if convertor is not none
            if not conv is None:
                #convert bytes to data object
                length = conv.get_byte_count()
                data = conv.from_bytes(b[:length])
                #remove used bytes
                b = b[length:]
            #append data object to data buffer
            data_buffer.append(data)
        #if data buffer has more than 1 item
        if len(data_buffer) > 1:
            #return data buffer
            return data_buffer
        else:
            #else return data object
            return data

#convertor classes
#=====================================================================================================
class BoolConvertor(object):
    '''
    classdocs
    '''
    def __init__(self):
        '''
        Constructor
        '''
        self.format = '?'
  
    def get_byte_count(self):
        return 1
    
    def from_bytes(self, b):
        return struct.unpack(self.format, b)[0]
    
    def to_bytes(self, data):
        return struct.pack(self.format, data)

class IntConvertor(object):
    '''
    classdocs
    '''
    def __init__(self, size):
        '''
        Constructor
        '''
        self.format = {
        1:  'c',
        2:  'h',
        4:  'i',
        8:  'q'
        }[size]
        if self.format is None:
            raise ValueError
        self.format = '>' + self.format
        self.size = size
    
    def get_byte_count(self):
        return self.size
    
    def from_bytes(self, b):
        return struct.unpack(self.format, b)[0]
    
    def to_bytes(self, data):
        return struct.pack(self.format, data)

class FloatConvertor(object):
    '''
    classdocs
    '''
    def __init__(self, size):
        '''
        Constructor
        '''
        self.format = {
        4:  'f',
        8:  'd'
        }[size]
        if self.format is None:
            raise ValueError
        self.format = '>' + self.format
        self.size = size
    
    def get_byte_count(self):
        return self.size
    
    def from_bytes(self, b):
        return struct.unpack(self.format, b)[0]
    
    def to_bytes(self, data):
        return struct.pack(self.format, data)

class StrConvertor(object):
    '''
    classdocs
    '''
    def __init__(self, size = 0):
        '''
        Constructor
        '''
        self.size = size
    
    def set_size(self, size):
        self.size = size
    
    def get_byte_count(self):
        return self.size
    
    def from_bytes(self, b):
        return str(b, 'utf-8')
    
    def to_bytes(self, data):
        return bytes(data, 'utf-8')
    
class ArrConvertor(object):
    '''
    classdocs
    '''
    def __init__(self, size=0, inner_conv=None):
        '''
        Constructor
        '''
        self.size = size
        self.inner_conv = inner_conv
    
    def set_size(self, size):
        self.size = size
    
    def get_size(self):
        return self.size
    
    def set_inner_convertor(self, inner_conv):
        self.inner_conv = inner_conv
    
    def get_inner_convertor(self):
        return self.inner_conv
    
    def get_byte_count(self):
        if self.inner_conv is None:
            return 0
        else:
            return self.size * self.inner_conv.get_byte_count()
    
    def from_bytes(self, b):
        data = list()
        for i in range(0, self.size):
            data.append(self.inner_conv.from_bytes(b[self.inner_conv.get_byte_count()*i:self.inner_conv.get_byte_count()*(i+1)]))
        return data
    
    def to_bytes(self, data):
        b = bytes()
        for item in data:
            b += self.inner_conv.to_bytes(item)
        return b

class FlightDetailsConvertor(object):
    '''
    classdocs
    '''
    def __init__(self):
        '''
        Constructor
        '''
        self.time_conv = IntConvertor(8)
        self.fare_conv = FloatConvertor(4)
        self.seat_conv = IntConvertor(4)
    
    def get_byte_count(self):
        return self.time_conv.get_byte_count() + self.fare_conv.get_byte_count() + self.seat_conv.get_byte_count()
    
    def from_bytes(self, b):
        data = FlightDetails()
        position = 0
        nextPosition = position + self.time_conv.get_byte_count()
        data.set_time(self.time_conv.from_bytes(b[position:nextPosition]))
        position = nextPosition
        nextPosition = position + self.fare_conv.get_byte_count()
        data.set_airfare(self.fare_conv.from_bytes(b[position:nextPosition]))
        position = nextPosition
        nextPosition = position + self.seat_conv.get_byte_count()
        data.set_available_seats(self.seat_conv.from_bytes(b[position:nextPosition]))
        return data
    
    def to_bytes(self, data):
        b = self.time_conv.to_bytes(data.get_time())
        b += self.fare_conv.to_bytes(data.get_airfare())
        b += self.seat_conv.to_bytes(data.get_available_seats())
        return b
#=====================================================================================================
    