package Marshaller;

import Entity.FlightDetails;

//implementation of an flight details data convertor
public class FlightDetailsConvertor implements Convertor{
	//returns the byte count of object to be converted
	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return Long.BYTES + Float.BYTES + Integer.BYTES;
	}

	//converts from a byte array to a object
	@Override
	public Object fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		//create empty object
		FlightDetails data = new FlightDetails();
		int position = 0;
		
		//convert byte array according to attributes of object
		//////////////////////////////////////////////////////////////////////////////////
		//long attribute conversion
		byte[] attributeBytes = new byte[Long.BYTES];
		System.arraycopy(bytes, position, attributeBytes, 0, attributeBytes.length);
		position += attributeBytes.length;
		//set attribute
		data.setTime((Long)new LongConvertor().fromBytes(attributeBytes));
		
		//float attribute conversion
		attributeBytes = new byte[Float.BYTES];
		System.arraycopy(bytes, position, attributeBytes, 0, attributeBytes.length);
		position += attributeBytes.length;
		//set attribute
		data.setAirfare((Float)new FloatConvertor().fromBytes(attributeBytes));
		
		//integer attribute conversion
		attributeBytes = new byte[Integer.BYTES];
		System.arraycopy(bytes, position, attributeBytes, 0, attributeBytes.length);
		position += attributeBytes.length;
		//set attribute
		data.setAvailableSeats((Integer)new IntegerConvertor().fromBytes(attributeBytes));
		//////////////////////////////////////////////////////////////////////////////////
		
		//return object
		return data;
	}

	//converts from a object to a corresponding byte array
	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return toBytes((FlightDetails)data);
	}
	
	//actual implementation of object to byte array conversion
	private byte[] toBytes(FlightDetails data){
		//create buffer for object byte array
		byte[] dataBytes = new byte[getByteCount()];
		byte[] attributeBytes;
		int position = 0;
		
		//convert each attribute to corresponding byte array
		//////////////////////////////////////////////////////////////////////////////////
		//byte array to long
		attributeBytes = new LongConvertor().toBytes(data.getTime());
		System.arraycopy(attributeBytes, 0, dataBytes, position, attributeBytes.length);
		position += attributeBytes.length;
		
		//byte array to float
		attributeBytes = new FloatConvertor().toBytes(data.getAirfare());
		System.arraycopy(attributeBytes, 0, dataBytes, position, attributeBytes.length);
		position += attributeBytes.length;
		
		//byte array to integer
		attributeBytes = new IntegerConvertor().toBytes(data.getAvailableSeats());
		System.arraycopy(attributeBytes, 0, dataBytes, position, attributeBytes.length);
		position += attributeBytes.length;
		//////////////////////////////////////////////////////////////////////////////////
		
		//return buffer
		return dataBytes;
	}

}
