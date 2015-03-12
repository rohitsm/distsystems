package Marshaller;

//implementation of a boolean convertor
public class BooleanConvertor implements Convertor{
	//returns the byte count of object to be converted
	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	//converts from a byte array to a object
	@Override
	public Object fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		if(bytes[0]==0){
			return false;
		}
		return true;
	}

	//converts from a object to a corresponding byte array
	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return fromBytes((Boolean) data);
	}
	
	//actual implementation of object to byte array conversion
	private byte[] fromBytes(Boolean data){
		//return byte value 1 if true, otherwise byte value 0
		return data?new byte[]{1}: new byte[]{0};
	}
	
}
