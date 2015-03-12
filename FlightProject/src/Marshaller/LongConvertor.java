package Marshaller;

//implementation of a long convertor (uses big endian ordering for conversion)
public class LongConvertor implements Convertor{
	//returns the byte count of object to be converted
	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return Long.BYTES;
	}
	
	//converts from a byte array to a object
	@Override
	public Object fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		Long data = (long) 0;
		//for each byte of long
		for(int i=0; i<data.BYTES; i++){
			//shift long left by one byte
			data = data << 8;
			//add byte to long
			data = data | (bytes[i] & 0xff);
		}
		//return long
		return data;
	}

	//converts from a object to a corresponding byte array
	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return toBytes((Long)data);
	}
	
	//actual implementation of object to byte array conversion
	private byte[] toBytes(Long data){
		int numOfBytes = data.BYTES;
		byte[] bytes = new byte[numOfBytes];
		//for each byte of long
		for(int i=0; i<numOfBytes; i++){
			//retrieve bytes from long, starting from the least significant byte
			//appends byte to front of array byte
			bytes[numOfBytes-1-i] = (byte)(data >> (8*i) & 0xff);
		}
		//returns byte array
		return bytes;
	}
}
