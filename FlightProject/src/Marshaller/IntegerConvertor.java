package Marshaller;

//implementation of a integer convertor (uses big endian ordering for conversion)
public class IntegerConvertor implements Convertor{
	//returns the byte count of object to be converted
	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return Integer.BYTES;
	}
	
	//converts from a byte array to a object
	@Override
	public Integer fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		Integer data = 0;
		//for each byte of integer
		for(int i=0; i<data.BYTES; i++){
			//shift integer left by one byte
			data = data << 8;
			//add byte to integer
			data = data | (bytes[i] & 0xff);
		}
		//returns integer
		return data;
	}

	//converts from a object to a corresponding byte array
	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return toBytes((Integer)data);
	}
	
	//actual implementation of object to byte array conversion
	private byte[] toBytes(Integer data){
		int numOfBytes = data.BYTES;
		byte[] bytes = new byte[numOfBytes];
		//for each byte of integer
		for(int i=0; i<numOfBytes; i++){
			//retrieve bytes from integer, starting from the least significant byte
			//appends byte to front of array byte
			bytes[numOfBytes-1-i] = (byte)(data >> (8*i) & 0xff);
		}
		//returns byte array
		return bytes;
	}
}
