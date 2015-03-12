package Marshaller;

//implementation of a string convertor
public class StringConvertor implements Convertor{
	//length of string to be converted
	int size;
	
	public StringConvertor(){
		size = 0;
	}
	
	public StringConvertor(int size){
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	//returns the byte count of object to be converted
	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return size;
	}

	//converts from a byte array to a object
	@Override
	public Object fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		return new String(bytes, 0, size);
	}

	//converts from a object to a corresponding byte array
	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return toBytes((String)data);
	}
	
	//actual implementation of object to byte array conversion
	public byte[] toBytes(String data){
		return data.getBytes();
	}

}
