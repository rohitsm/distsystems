package Marshaller;

//implementation of a float convertor
public class FloatConvertor implements Convertor{
	//returns the byte count of object to be converted
	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return Float.BYTES;
	}
	
	//converts from a byte array to a object
	@Override
	public Object fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		//converts from byte array to integer
		//converts from integer to float
		return Float.intBitsToFloat(new IntegerConvertor().fromBytes(bytes));
	}
	
	//converts from a object to a corresponding byte array
	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return toBytes((Float)data);
	}
	
	//actual implementation of object to byte array conversion
	private byte[] toBytes(Float data){
		//converts from float to integer
		//converts from integer to byte array
		return new IntegerConvertor().toBytes(Float.floatToRawIntBits(data));
	}

}
