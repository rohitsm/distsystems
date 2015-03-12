package Marshaller;

//interface for byte convertor
public interface Convertor {
	//returns the byte count of object to be converted
	public int getByteCount();
	
	//converts from a byte array to a object
	public Object fromBytes(byte[] bytes);
	
	//converts from a object to a corresponding byte array
	public byte[] toBytes(Object data);
}
