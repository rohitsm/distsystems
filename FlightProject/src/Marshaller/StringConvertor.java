package Marshaller;

public class StringConvertor implements Convertor{
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

	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public Object fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		return new String(bytes, 0, size);
	}

	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return toBytes((String)data);
	}
	
	public byte[] toBytes(String data){
		return data.getBytes();
	}

}
