package Marshaller;

import Entity.FlightDetails;

public class FlightDetailsConvertor implements Convertor{
	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return Long.BYTES + Float.BYTES + Integer.BYTES;
	}

	@Override
	public Object fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		FlightDetails data = new FlightDetails();
		int position = 0;
		
		byte[] attributeBytes = new byte[Long.BYTES];
		System.arraycopy(bytes, position, attributeBytes, 0, attributeBytes.length);
		position += attributeBytes.length;
		data.setTime((Long)new LongConvertor().fromBytes(attributeBytes));
		
		attributeBytes = new byte[Float.BYTES];
		System.arraycopy(bytes, position, attributeBytes, 0, attributeBytes.length);
		position += attributeBytes.length;
		data.setAirfare((Float)new FloatConvertor().fromBytes(attributeBytes));
		
		attributeBytes = new byte[Integer.BYTES];
		System.arraycopy(bytes, position, attributeBytes, 0, attributeBytes.length);
		position += attributeBytes.length;
		data.setAvailableSeats((Integer)new IntegerConvertor().fromBytes(attributeBytes));
		
		return data;
	}

	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return toBytes((FlightDetails)data);
	}
	
	private byte[] toBytes(FlightDetails data){
		byte[] dataBytes = new byte[getByteCount()];
		byte[] attributeBytes;
		int position = 0;
		attributeBytes = new LongConvertor().toBytes(data.getTime());
		System.arraycopy(attributeBytes, 0, dataBytes, position, attributeBytes.length);
		position += attributeBytes.length;
		
		attributeBytes = new FloatConvertor().toBytes(data.getAirfare());
		System.arraycopy(attributeBytes, 0, dataBytes, position, attributeBytes.length);
		position += attributeBytes.length;
		
		attributeBytes = new IntegerConvertor().toBytes(data.getAvailableSeats());
		System.arraycopy(attributeBytes, 0, dataBytes, position, attributeBytes.length);
		position += attributeBytes.length;
		
		return dataBytes;
	}

}
